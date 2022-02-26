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

    public void getAllStudents(StudentListListener studentListListener) {
        this.studentListListener = studentListListener;
        getDataBaseReference(STUDENTS_REFERENCE).addChildEventListener(studentEventListener);
    }

    public void getAllFranchises(FranchiseListListener franchiseListListener) {
        this.franchiseListListener = franchiseListListener;
        getDataBaseReference(USER_REFERENCE).addChildEventListener(franchiseEventListener);
    }

    public void getAllTransactions(TransactionListListener transactionListListener) {
        this.transactionListListener = transactionListListener;
        getDataBaseReference(TRANSACTIONS_REFERENCE).addChildEventListener(transactionsEventListener);
    }

    public void getAllStocks(StockListListener stockListListener) {
        this.stockListListener = stockListListener;
        DatabaseReference databaseReference = getDataBaseReference(STOCK_REFERENCE);
        databaseReference.removeEventListener(stockEventListener);
        databaseReference.addChildEventListener(stockEventListener);
    }

    public void updateStock(Stock stock, StockListListener stockListListener) {
        this.stockListListener = stockListListener;
        DatabaseReference databaseReference = getDataBaseReference(STOCK_REFERENCE);
        databaseReference.child(stock.getName()).setValue(stock);
    }

    public void updateStock(Student student, List<Stock> stocks) {
        for (String name : student.getItems()) {
            Stock tempStock = new Stock();
            tempStock.setName(name);
            if (stocks.contains(tempStock)) {
                Stock inStock = stocks.get(stocks.indexOf(tempStock));
                inStock.setQuantity(inStock.getQuantity() - 1);
                getDataBaseReference(STOCK_REFERENCE).child(name).setValue(inStock);
                StockTransaction stockTransaction = new StockTransaction(inStock.getName(), StockTransaction.TYPE.ADD.ordinal(), 1, inStock.getQuantity(), inStock.getQuantity() - 1, UIUtils.getDate(), "");
                addTransaction(stockTransaction, null, null);
            }
        }
    }

    ChildEventListener userDetailsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
           /* User user = dataSnapshot.getValue(User.class);
            franchiseListListener.onFranchiseListLoaded(user);*/
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

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

    ChildEventListener franchiseEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            User user = dataSnapshot.getValue(User.class);
            franchiseListListener.onFranchiseListLoaded(user);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

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

    ChildEventListener studentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Student student = dataSnapshot.getValue(Student.class);
            studentListListener.onStudentListLoaded(student);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

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

    ChildEventListener transactionsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            StockTransaction stockTransaction = dataSnapshot.getValue(StockTransaction.class);
            transactionListListener.onTransactionsLoaded(stockTransaction);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

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

    ChildEventListener stockEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Stock stock = dataSnapshot.getValue(Stock.class);
            stockListListener.onStockListLoaded(stock);
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
        getDataBaseReference(STOCK_REFERENCE).removeEventListener(stockEventListener);
    }

    public void logout() {
        mAuth.signOut();
    }
}
