package com.example.zencash.service;

import com.example.zencash.dto.*;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.TransactionRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.xml.bind.DatatypeConverter.parseDate;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BudgetRepository budgetRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BudgetService budgetService;


    @Autowired private AIService aiService;

    public TransactionResponse createTransaction(TransactionRequest request) {
        Budget budget = budgetService.getBudgetById(request.getBudgetId());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setNote(request.getNote());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());
        transaction.setCreateAt(LocalDateTime.now());
        transaction.setBudget(budget);
        transaction.setCategory(category);

        transaction = transactionRepository.save(transaction);
        budgetService.updateRemainingAmount(budget);

        return mapToResponse(transaction);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        existing.setAmount(request.getAmount());
        existing.setNote(request.getNote());
        existing.setDate(request.getDate());
        existing.setType(request.getType());
        existing.setUpdateAt(LocalDateTime.now());

        if (!existing.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            existing.setCategory(newCategory);
        }

        Transaction updated = transactionRepository.save(existing);
        budgetService.updateRemainingAmount(updated.getBudget());

        return mapToResponse(updated);
    }

    public boolean deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        Budget budget = transaction.getBudget();
        transactionRepository.delete(transaction);
        budgetService.updateRemainingAmount(budget);
        return true;
    }

    public Map<String, BigDecimal> calculateUserIncomeExpense(User user) {
        List<Transaction> transactions = transactionRepository.findAllByUser(user);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if ("INCOME".equalsIgnoreCase(tx.getType())) {
                totalIncome = totalIncome.add(tx.getAmount());
            } else if ("EXPENSE".equalsIgnoreCase(tx.getType())) {
                totalExpense = totalExpense.add(tx.getAmount());
            }
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("income", totalIncome);
        result.put("expense", totalExpense);
        return result;
    }


    public List<TransactionResponse> getByBudget(Long budgetId) {
        return transactionRepository.findByBudgetId(budgetId)
                .stream().map(this::mapToResponse)
                .toList();
    }

    public List<CategoryGroupStatisticResponse> getCategoryGroupStatistics(Long budgetId) {
        return transactionRepository.getStatisticsByBudgetId(budgetId);
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getBudget().getId(),
                tx.getCategory().getId(),
                tx.getAmount(),
                tx.getType(),
                tx.getNote(),
                tx.getDate()
        );
    }
    private BigDecimal parseAmount(String text) {
        // Xác định loại tiền
        boolean isUSD = text.contains("$") || text.toLowerCase().contains("usd");
        boolean isVND = text.contains("đ") || text.toLowerCase().contains("vnd") || text.contains("₫");

        // Regex tìm số có thể có cả dấu ngăn cách hoặc phần thập phân
        Pattern pattern = Pattern.compile("(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d+)?|\\d+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String raw = matcher.group(1).trim();

            if (isUSD) {
                // USD thường dùng dấu phẩy cho nghìn, chấm cho phần thập phân
                if (raw.contains(",")) {
                    long commaCount = raw.chars().filter(ch -> ch == ',').count();
                    long dotCount = raw.chars().filter(ch -> ch == '.').count();

                    if (commaCount == 1 && dotCount == 0) {
                        // Ví dụ: "50,00" (kiểu EU) => "50.00"
                        raw = raw.replace(",", ".");
                    } else {
                        // Ví dụ: "1,000.50" => "1000.50"
                        raw = raw.replace(",", "");
                    }
                }
            } else {
                // VND thường dùng dấu chấm hoặc dấu phẩy cho hàng nghìn => bỏ hết
                raw = raw.replace(".", "").replace(",", "");
            }

            return new BigDecimal(raw);
        }

        throw new AppException(ErrorCode.INVALID_DATA);
    }



    private String parseNote(String text) {
        // Tùy bạn, hoặc chỉ cần cắt dòng đầu tiên
        return text.lines().findFirst().orElse("Invoice");
    }

    private LocalDate parseDate(String text) {
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group(1));
        }
        return LocalDate.now(); // fallback nếu không tìm được
    }

    public TransactionResponse createFromInvoiceText(String text, String email) {
        // Gửi văn bản OCR cho AI để xử lý thông tin
        String refinedText = aiService.sendMessageToAI(text, email);

        // Tiến hành xử lý refinedText để tách các trường cần thiết như amount, date, note...
        String[] lines = refinedText.split("\n");

        BigDecimal amount = BigDecimal.ZERO;
        String note = "";
        LocalDate date = LocalDate.now();

        for (String line : lines) {
            line = line.trim();

            if (line.toLowerCase().startsWith("date:") || line.toLowerCase().contains("ngày")) {
                date = parseDate(line);
            }  else if (line.toLowerCase().contains("amount") || line.toLowerCase().contains("số tiền")) {

            amount = parseAmount(line);
            } else if (line.toLowerCase().contains("note")) {
                note = parseNote(line);
            }
        }
        System.out.println("RefinedText from AI:\n" + refinedText);


        // Tạo request DTO cho invoice
        InvoiceTransactionRequest invoiceRequest = new InvoiceTransactionRequest();
        invoiceRequest.setAmount(amount);
        invoiceRequest.setNote(note);
        invoiceRequest.setDate(date);
        invoiceRequest.setType("EXPENSE");
        invoiceRequest.setEmail(email);
        System.out.println("Parsed amount = " + amount);

        return createTransactionFromInvoice(invoiceRequest);
    }

    public TransactionResponse createTransactionFromInvoice(InvoiceTransactionRequest request) {
        User user = getUserByEmail(request.getEmail());

        Budget budget = (request.getBudgetId() != null)
                ? budgetRepository.findById(request.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND))
                : budgetRepository.findFirstByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        Category category = (request.getCategoryId() != null)
                ? categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND))
                : categoryRepository.findFirstByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        TransactionRequest txRequest = new TransactionRequest();
        txRequest.setAmount(request.getAmount());
        txRequest.setNote(request.getNote());
        txRequest.setDate(request.getDate());
        txRequest.setType(request.getType());
        txRequest.setBudgetId(budget.getId());
        txRequest.setCategoryId(category.getId());

        return createTransaction(txRequest);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public InvoiceExtractedDataResponse extractInvoiceData(String text, String email) {
        // Gửi văn bản OCR đến AI để refine
        String refinedText = aiService.sendMessageToAI(text, email);
        System.out.println("RefinedText from AI:\n" + refinedText);

        // Mặc định
        BigDecimal amount = BigDecimal.ZERO;
        String note = "";
        LocalDate date = LocalDate.now();

        // Phân tích văn bản đã refine từ AI
        String[] lines = refinedText.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.toLowerCase().contains("amount") || line.toLowerCase().contains("số tiền")) {
                amount = parseAmount(line); // tự viết hàm parseAmount
            } else if (line.toLowerCase().contains("date") || line.toLowerCase().contains("ngày")) {
                date = parseDate(line); // tự viết hàm parseDate
            } else if (line.toLowerCase().contains("note") || line.toLowerCase().contains("ghi chú")) {
                note = parseNote(line); // tự viết hàm parseNote
            }
        }

        // Lấy budgetId và categoryId mặc định (có thể để client chọn lại sau)
        User user = getUserByEmail(email);

        Long budgetId = budgetRepository.findFirstByUser(user)
                .map(Budget::getId)
                .orElse(null);

        Long categoryId = categoryRepository.findFirstByUser(user)
                .map(Category::getId)
                .orElse(null);

        // Trả về object để client confirm
        return InvoiceExtractedDataResponse.builder()
                .amount(amount)
                .note(note)
                .date(date)
                .type("EXPENSE")
                .budgetId(budgetId)
                .categoryId(categoryId)
                .build();
    }
    public List<TransactionResponse> getTopExpenses(int limit, User user) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Transaction> expenses = transactionRepository.findByBudget_UserAndTypeOrderByAmountDesc(user, "EXPENSE", pageable);
        return expenses.stream().map(this::mapToResponse).toList();
    }
//    public List<TransactionResponse> getTopExpenses(int limit, User user) {
//        List<Transaction> expenses = transactionRepository.findTopByUserAndTypeOrderByAmountDesc(user, "EXPENSE", limit);
//        return expenses.stream()
//                .map(this::mapToResponse)
//                .toList();
//    }


}



