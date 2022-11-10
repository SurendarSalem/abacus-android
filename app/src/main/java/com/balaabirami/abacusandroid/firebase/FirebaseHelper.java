package com.balaabirami.abacusandroid.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockAdjustment;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.adapter.StockListAdapter;
import com.balaabirami.abacusandroid.utils.DataLoadListener;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.FranchiseListListener;
import com.balaabirami.abacusandroid.viewmodel.LastStudentIdListener;
import com.balaabirami.abacusandroid.viewmodel.OrderListListener;
import com.balaabirami.abacusandroid.viewmodel.StockListListener;
import com.balaabirami.abacusandroid.viewmodel.StudentListListener;
import com.balaabirami.abacusandroid.viewmodel.TransactionListListener;
import com.balaabirami.abacusandroid.viewmodel.UserDetailListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.SocketHandler;

public class FirebaseHelper {

    public static final String USER_REFERENCE = "users";
    public static final String STUDENTS_REFERENCE = "students";
    public static final String STOCK_ADJUSTMENT_REFERENCE = "stockAdjustments";
    public static final String ORDER_REFERENCE = "orders";
    public static final String STOCK_REFERENCE = "stock";
    public static final String TRANSACTIONS_REFERENCE = "transactions";
    public static final String LAST_STUDENT_IDS = "lastStudentId";
    FirebaseAuth mAuth;
    private StudentListListener studentListListener;
    private UserDetailListener userDetailListener;
    private LastStudentIdListener lastStudentIdListener;
    private FranchiseListListener franchiseListListener;
    private StockListListener stockListListener;
    private ValueEventListener stockListsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Stock> stocks = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Stock stock = postSnapshot.getValue(Stock.class);
                stocks.add(stock);
            }
            stockListListener.onStockListLoaded(stocks);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            stockListListener.onStockListLoaded(new ArrayList<>());
        }
    };
    private FirebaseDatabase mDatabase;
    private TransactionListListener transactionListListener;
    private OrderListListener orderListListener;

    public void init(String path) {
        //databaseReference = mDatabase.getReference(path);
        mAuth = FirebaseAuth.getInstance();
    }

    public void addUser(User user, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(USER_REFERENCE).child(user.getFirebaseId()).setValue(user).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    private DatabaseReference getDataBaseReference(String path) {
        mDatabase = FirebaseDatabase.getInstance();
        return mDatabase.getReference(path);
    }

    public void enrollStudent(Student student, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        if (!UIUtils.IS_DATA_IMPORT) {
            getLastStudentId(new LastStudentIdListener() {
                @Override
                public void onLastStudentIdLoaded(int lastStudentId) {
                    lastStudentId += 1;
                    student.setStudentId(String.valueOf(lastStudentId));
                    getDataBaseReference(STUDENTS_REFERENCE).child(student.getStudentId()).setValue(student).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            getDataBaseReference(STUDENTS_REFERENCE).child(student.getStudentId()).setValue(student).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
        }
    }

    public void createStockStock(StockAdjustment stockAdjustment, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(STOCK_ADJUSTMENT_REFERENCE).child(Objects.requireNonNull(stockAdjustment.getId())).setValue(stockAdjustment).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void getAllStockAdjustments(DataLoadListener<StockAdjustment> dataLoadListener) {
        getDataBaseReference(STOCK_ADJUSTMENT_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<StockAdjustment> items = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    StockAdjustment t = postSnapshot.getValue(StockAdjustment.class);
                    items.add(t);
                }
                dataLoadListener.onDataLoaded(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void order(Order order, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(ORDER_REFERENCE).child(order.getOrderId()).setValue(order).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void getAllOrders(User currentUser, OrderListListener orderListListener) {
        this.orderListListener = orderListListener;
        OrderEventListener orderEventListener = new OrderEventListener(currentUser);
        getDataBaseReference(ORDER_REFERENCE).addValueEventListener(orderEventListener);
    }

    public void createUser(User user, OnCompleteListener onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail().trim(), user.getPassword().trim()).addOnCompleteListener(onCompleteListener);
    }

    public void login(User user, OnCompleteListener onCompleteListener) {
        mAuth.signInWithEmailAndPassword(user.getEmail().trim(), user.getPassword().trim()).addOnCompleteListener(onCompleteListener);
    }

    public void getUserDetail(User user, UserDetailListener userDetailListener) {
        this.userDetailListener = userDetailListener;
        DatabaseReference databaseReference = getDataBaseReference(USER_REFERENCE);
        databaseReference.child(user.getFirebaseId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userDetailListener.onUserDetailLoaded(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLastStudentId(LastStudentIdListener lastStudentIdListener) {
        this.lastStudentIdListener = lastStudentIdListener;
        DatabaseReference databaseReference = getDataBaseReference(LAST_STUDENT_IDS);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int lastStudentId = dataSnapshot.getValue(Integer.class);
                lastStudentIdListener.onLastStudentIdLoaded(lastStudentId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lastStudentIdListener.onError(error.getMessage());
            }
        });
    }

    public void updateLastStudentId(int lastStudentId) {
        DatabaseReference databaseReference = getDataBaseReference(LAST_STUDENT_IDS);
        databaseReference.setValue(lastStudentId);
    }

    public void getAllStudents(User currentUser, StudentListListener studentListListener) {
        this.studentListListener = studentListListener;
        StudentEventListener studentEventListener = new StudentEventListener(currentUser);
        getDataBaseReference(STUDENTS_REFERENCE).addValueEventListener(studentEventListener);
    }

    public void getAllFranchises(FranchiseListListener franchiseListListener) {
        this.franchiseListListener = franchiseListListener;
        FranchiseEventListener franchiseEventListener = new FranchiseEventListener();
        getDataBaseReference(USER_REFERENCE).addValueEventListener(franchiseEventListener);
    }

    public void getAllTransactions(TransactionListListener transactionListListener, Stock stock, User currentUser) {
        this.transactionListListener = transactionListListener;
        TransactionsEventListener transactionsEventListener = new TransactionsEventListener(stock, currentUser);
        getDataBaseReference(TRANSACTIONS_REFERENCE).addValueEventListener(transactionsEventListener);
    }

    public void getAllStocks(StockListListener stockListListener) {
        this.stockListListener = stockListListener;
        DatabaseReference databaseReference = getDataBaseReference(STOCK_REFERENCE);
        databaseReference.removeEventListener(stockListsListener);
        databaseReference.addValueEventListener(stockListsListener);
    }

    public void updateStudent(Student student, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DatabaseReference databaseReference = getDataBaseReference(STUDENTS_REFERENCE);
        databaseReference.child(student.getStudentId()).setValue(student).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    /* Updating from Stock List Screen */
    public void updateStock(Stock stock, StockListListener stockListListener, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        this.stockListListener = stockListListener;
        DatabaseReference databaseReference = getDataBaseReference(STOCK_REFERENCE);
        databaseReference.child(stock.getName()).setValue(stock).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void updateStock(Stock stock, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DatabaseReference databaseReference = getDataBaseReference(STOCK_REFERENCE);
        databaseReference.child(stock.getName()).setValue(stock).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    /* Updating from Enroll Screen */
    public void updateStock(Student student, List<Stock> stocks, User currentUser) {
        for (String name : student.getItems()) {
            Stock tempStock = new Stock();
            tempStock.setName(name);
            if (stocks.contains(tempStock)) {
                Stock inStock = stocks.get(stocks.indexOf(tempStock));
                inStock.setQuantity(inStock.getQuantity() - 1);
                getDataBaseReference(STOCK_REFERENCE).child(name).setValue(inStock);
                StockTransaction stockTransaction = new StockTransaction(inStock.getName(), StockTransaction.TYPE.REMOVE.ordinal(), inStock.getQuantity(), 1, 0, UIUtils.getDate(), currentUser.getId(), currentUser.getName(), student.getState(), StockTransaction.OWNER_TYPE_FRANCHISE);
                addTransaction(stockTransaction, null, null);
            }
        }
    }

    /* Updating from Order Screen */
    public void updateStock(List<String> books, List<Stock> stocks, User currentUser, Student student) {
        for (String name : books) {
            Stock tempStock = new Stock();
            tempStock.setName(name);
            if (stocks.contains(tempStock)) {
                int index = stocks.indexOf(tempStock);
                if (index >= 0) {
                    Stock inStock = stocks.get(index);
                    inStock.setQuantity(inStock.getQuantity() - 1);
                    getDataBaseReference(STOCK_REFERENCE).child(name).setValue(inStock);
                    StockTransaction stockTransaction = new StockTransaction(inStock.getName(), StockTransaction.TYPE.REMOVE.ordinal(),
                            inStock.getQuantity(), 1, 0,
                            UIUtils.getDate(), currentUser.getId(), currentUser.getName(), student.getState(),
                            StockTransaction.OWNER_TYPE_FRANCHISE);
                    addTransaction(stockTransaction, null, null);
                }
            } else {
                Log.d("Suren", "No data found" + stocks.contains(tempStock));
            }
        }
    }

    class StudentEventListener implements ValueEventListener {
        User currentUser;

        public StudentEventListener(User currentUser) {
            this.currentUser = currentUser;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Student> students = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Student student = postSnapshot.getValue(Student.class);
                if (student != null) {
                    if (currentUser.getAccountType() == User.TYPE_MASTER_FRANCHISE) {
                        if (currentUser.getState().equalsIgnoreCase(student.getState())) {
                            students.add(student);
                        }
                    } else if (currentUser.getId().equals(student.getFranchise()) || currentUser.getAccountType() == User.TYPE_ADMIN) {
                        students.add(student);
                    }
                }
            }
            UIUtils.sort(students);
            studentListListener.onStudentListLoaded(students);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            studentListListener.onStudentListLoaded(new ArrayList<>());
        }
    }

    class FranchiseEventListener implements ValueEventListener {


        public FranchiseEventListener() {
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<User> franchises = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                User franchise = postSnapshot.getValue(User.class);
                assert franchise != null;
                if (franchise.getAccountType() != User.TYPE_ADMIN) {
                    franchises.add(franchise);
                }
            }
            UIUtils.sortFranchise(franchises);
            User header = new User();
            header.setName("Select a Franchise");
            header.setAccountType(User.TYPE_HEADER);
            franchises.add(0, header);
            franchiseListListener.onFranchiseListLoaded(franchises);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            franchiseListListener.onFranchiseListLoaded(new ArrayList<>());
        }
    }

    class TransactionsEventListener implements ValueEventListener {

        private final User currentUser;
        Stock stock;

        public TransactionsEventListener(Stock stock, User currentUser) {
            this.stock = stock;
            this.currentUser = currentUser;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<StockTransaction> stockTransactions = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                StockTransaction stockTransaction = postSnapshot.getValue(StockTransaction.class);
                if (stock.getName().equalsIgnoreCase(stockTransaction.getName())) {
                    if (currentUser.getAccountType() == User.TYPE_MASTER_FRANCHISE) {
                        if (currentUser.getState().equalsIgnoreCase(stockTransaction.getStudentState())) {
                            stockTransactions.add(stockTransaction);
                        }
                    } else {
                        stockTransactions.add(stockTransaction);
                    }
                }
            }
            transactionListListener.onTransactionsLoaded(stockTransactions);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    class OrderEventListener implements ValueEventListener {
        User currentUser;

        public OrderEventListener(User currentUser) {
            this.currentUser = currentUser;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Order> orders = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Order order = postSnapshot.getValue(Order.class);
                if (order != null) {
                    if (currentUser.isIsAdmin()) {
                        orders.add(order);
                    } else if (currentUser.getAccountType() == User.TYPE_MASTER_FRANCHISE) {
                        if (order.getState().equalsIgnoreCase(currentUser.getState()))
                            orders.add(order);
                    } else if (currentUser.getName().equalsIgnoreCase(order.getFranchiseName())) {
                        orders.add(order);
                    }
                }
            }
            orderListListener.onOrderListLoaded(orders);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }


    public void approveStudent(Student student, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        DatabaseReference databaseReference = getDataBaseReference(STUDENTS_REFERENCE);
        databaseReference.child(student.getStudentId()).setValue(student).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void approveFranchise(User franchise, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        franchise.setApproved(true);
        DatabaseReference databaseReference = getDataBaseReference(USER_REFERENCE);
        databaseReference.child(franchise.getFirebaseId()).setValue(franchise).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void addTransaction(StockTransaction transaction, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(TRANSACTIONS_REFERENCE).child(StockTransaction.createID()).setValue(transaction).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void clearListener() {
        // getDataBaseReference(STOCK_REFERENCE).removeEventListener(stockEventListener);
    }

    public void logout() {
        mAuth.signOut();
    }
}
