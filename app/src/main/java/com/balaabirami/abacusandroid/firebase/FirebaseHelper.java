package com.balaabirami.abacusandroid.firebase;

import androidx.annotation.NonNull;

import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.adapter.StockListAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.FranchiseListListener;
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
import java.util.List;
import java.util.logging.SocketHandler;

public class FirebaseHelper {

    public static final String USER_REFERENCE = "users";
    public static final String STUDENTS_REFERENCE = "students";
    public static final String ORDER_REFERENCE = "orders";
    public static final String STOCK_REFERENCE = "stock";
    public static final String TRANSACTIONS_REFERENCE = "transactions";
    FirebaseAuth mAuth;
    private StudentListListener studentListListener;
    private UserDetailListener userDetailListener;
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

    public void init(String path) {
        //databaseReference = mDatabase.getReference(path);
        mAuth = FirebaseAuth.getInstance();
    }

    public void addUser(User user, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(USER_REFERENCE).child(user.createID()).setValue(user).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    private DatabaseReference getDataBaseReference(String path) {
        mDatabase = FirebaseDatabase.getInstance();
        return mDatabase.getReference(path);
    }

    public void enrollStudent(Student student, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(STUDENTS_REFERENCE).child(student.createID()).setValue(student).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void order(Order order, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        getDataBaseReference(ORDER_REFERENCE).child(order.getOrderId()).setValue(order).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void createUser(User user, OnCompleteListener onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(onCompleteListener);
    }

    public void login(User user, OnCompleteListener onCompleteListener) {
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(onCompleteListener);
    }

    public void getUserDetail(User user, UserDetailListener userDetailListener) {
        this.userDetailListener = userDetailListener;
        DatabaseReference databaseReference = getDataBaseReference(USER_REFERENCE);
        databaseReference.child(user.getEmail().substring(0, user.getEmail().indexOf("@"))).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void updateStock(Stock stock, StockListListener stockListListener, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        this.stockListListener = stockListListener;
        DatabaseReference databaseReference = getDataBaseReference(STOCK_REFERENCE);
        databaseReference.child(stock.getName()).setValue(stock).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void updateStock(Student student, List<Stock> stocks, User currentUser) {
        for (String name : student.getItems()) {
            Stock tempStock = new Stock();
            tempStock.setName(name);
            if (stocks.contains(tempStock)) {
                Stock inStock = stocks.get(stocks.indexOf(tempStock));
                inStock.setQuantity(inStock.getQuantity() - 1);
                getDataBaseReference(STOCK_REFERENCE).child(name).setValue(inStock);
                StockTransaction stockTransaction = new StockTransaction(inStock.getName(), StockTransaction.TYPE.ADD.ordinal(), inStock.getQuantity(), 1, 0, UIUtils.getDate(), currentUser.getId(), currentUser.getName(), student.getState());
                addTransaction(stockTransaction, null, null);
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
                if (currentUser.getAccountType() == User.TYPE_MASTER_FRANCHISE) {
                    if (currentUser.getState().equalsIgnoreCase(student.getState())) {
                        students.add(student);
                    }
                } else if (currentUser.getId().equals(student.getFranchise()) || currentUser.getAccountType() == User.TYPE_ADMIN) {
                    students.add(student);
                }
            }
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
            User header = new User();
            header.setName("Select a Franchise");
            header.setAccountType(User.TYPE_HEADER);
            franchises.add(header);
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                User franchise = postSnapshot.getValue(User.class);
                if (franchise.getAccountType() != User.TYPE_ADMIN) {
                    franchises.add(franchise);
                }
            }
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

    ChildEventListener stockEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            /*Stock stock = dataSnapshot.getValue(Stock.class);
            stockListListener.onStockListLoaded(stock);*/
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Stock stock = dataSnapshot.getValue(Stock.class);
            stockListListener.onStockUpdated(stock);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public void approveStudent(Student student, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        DatabaseReference databaseReference = getDataBaseReference(STUDENTS_REFERENCE);
        databaseReference.child(student.getEmail().substring(0, student.getEmail().indexOf("@"))).setValue(student).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void updateStudent(Student student, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        DatabaseReference databaseReference = getDataBaseReference(STUDENTS_REFERENCE);
        databaseReference.child(student.getEmail().substring(0, student.getEmail().indexOf("@"))).setValue(student);
    }

    public void approveFranchise(User franchise, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        franchise.setApproved(true);
        DatabaseReference databaseReference = getDataBaseReference(USER_REFERENCE);
        databaseReference.child(franchise.getEmail().substring(0, franchise.getEmail().indexOf("@"))).setValue(franchise).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
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
