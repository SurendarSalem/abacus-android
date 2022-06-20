package com.balaabirami.abacusandroid.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.adapter.multiadapter.FilterAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterDialog extends AppCompatDialog {
    Spinner spStates;
    Spinner spFranchise;
    Spinner spStocks;
    Spinner spStudents;
    Spinner spLevels;
    Spinner spBooks;
    FilterListener filterListener;
    AppCompatButton btnApply;
    AppCompatButton btnClear;
    AppCompatEditText etFromState;
    AppCompatEditText etEndDate;
    LinearLayoutCompat llDate;
    String[] filters = new String[2];
    private FilterAdapter<Stock> stocksAdapter;
    private FilterAdapter<User> franchiseAdapter;
    private FilterAdapter<State> stateAdapter;
    private FilterAdapter<Student> studentAdapter;
    private FilterAdapter<Book> booksAdapter;
    private FilterAdapter<Level> levelAdapter;
    List<Stock> selectedItems = null;
    List<Student> selectedStudents = null;
    List<Level> selectedLevels = null;
    List<Book> selectedBooks = null;
    List<State> selectedStates = null;
    List<User> selectedFranchises = null;
    String[] dates = getTodayDate();
    AppCompatCheckBox cbFilterDate;
    private boolean isDateFilterApplied;

    List<String> selectedItemNames = new ArrayList<>();
    List<String> selectedStudentNames = new ArrayList<>();
    List<String> selectedLevelNames = new ArrayList<>();
    List<String> selectedBookNames = new ArrayList<>();
    List<String> selectedStateNames = new ArrayList<>();
    List<String> selectedFranchiseNames = new ArrayList<>();

    public FilterDialog(Context context) {
        super(context);
        initDialog(context);
        initViews();
    }

    private void initDialog(Context context) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.filter_dialog);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initViews() {
        spStates = findViewById(R.id.sp_state);
        spFranchise = findViewById(R.id.sp_franchise);
        spStocks = findViewById(R.id.sp_stocks);
        spStudents = findViewById(R.id.sp_students);
        spLevels = findViewById(R.id.sp_levels);
        spBooks = findViewById(R.id.sp_books);
        btnApply = findViewById(R.id.btn_apply);
        btnClear = findViewById(R.id.btn_clear);
        llDate = findViewById(R.id.ll_date);
        etFromState = findViewById(R.id.et_from_date);
        etEndDate = findViewById(R.id.et_end_date);
        etFromState.addTextChangedListener(new DateTextWatchListener(etFromState));
        etEndDate.addTextChangedListener(new DateTextWatchListener(etEndDate));
        cbFilterDate = findViewById(R.id.cb_filter_date);
        btnApply.setOnClickListener(view -> {
            if (stateAdapter != null) {
                selectedStates = stateAdapter.getSelectedObjects();
                for (State state : selectedStates) {
                    selectedStateNames.add(state.getName());
                }
            }
            if (franchiseAdapter != null) {
                selectedFranchises = franchiseAdapter.getSelectedObjects();
                for (User franchise : selectedFranchises) {
                    selectedFranchiseNames.add(franchise.getName());
                }
            }
            if (stocksAdapter != null) {
                selectedItems = stocksAdapter.getSelectedObjects();
            }
            if (studentAdapter != null) {
                selectedStudents = studentAdapter.getSelectedObjects();
                for (Student student : selectedStudents) {
                    selectedStudentNames.add(student.getStudentId());
                }
            }
            if (levelAdapter != null) {
                selectedLevels = levelAdapter.getSelectedObjects();
                for (Level level : selectedLevels) {
                    selectedLevelNames.add(level.getName());
                }
            }
            if (booksAdapter != null) {
                selectedBooks = booksAdapter.getSelectedObjects();
            }
            if (isDateFilterApplied) {
                dates[0] = etFromState.getText().toString();
                dates[1] = etEndDate.getText().toString();
                if (UIUtils.isDateNotValid(dates[0]) || UIUtils.isDateNotValid(dates[1])) {
                    UIUtils.showToast(getContext(), "Please enter valid From Date and To Date");
                    return;
                } else {
                    Date startDate = UIUtils.convertStringToDate(dates[0]);
                    Date endDate = UIUtils.convertStringToDate(dates[1]);
                    if (startDate == null || endDate == null) {
                        UIUtils.showToast(getContext(), "Please enter valid From Date and To Date");
                        return;
                    } else if (endDate.before(startDate)) {
                        UIUtils.showToast(getContext(), "From date should not be greater than To Date");
                        return;
                    }
                }
            }
            filterListener.onFilterApplied(selectedStates, selectedFranchises, selectedItems, selectedStudents, selectedLevels, selectedBooks, isDateFilterApplied ? dates : null);
            filterListener.onFilterSelected(selectedStateNames, selectedFranchiseNames, selectedItemNames, selectedStudentNames, selectedLevelNames, selectedBookNames, isDateFilterApplied ? dates : null);
        });
        btnClear.setOnClickListener(view -> {
            clearAllFilter();
            filterListener.onFilterCleared();
        });
    }

    public void clearAllFilter() {
        if (stateAdapter != null) {
            spStates.setSelection(0);
            stateAdapter.clearAll();
        }

        if (franchiseAdapter != null) {
            spFranchise.setSelection(0);
            franchiseAdapter.clearAll();
        }

        if (stocksAdapter != null) {
            spStocks.setSelection(0);
            stocksAdapter.clearAll();
        }

        if (levelAdapter != null) {
            spLevels.setSelection(0);
            levelAdapter.clearAll();
        }

        if (booksAdapter != null) {
            spBooks.setSelection(0);
            booksAdapter.clearAll();
        }
        if (studentAdapter != null) {
            spStudents.setSelection(0);
            studentAdapter.clearAll();
        }
        cbFilterDate.setChecked(false);
    }

    public void setAdapters(List<State> states1, List<User> franchises1, List<Stock> stocks1, List<Student> students1, List<Level> levels1, List<Book> books1, boolean showDateFilter) {
        List<State> states = states1 == null ? new ArrayList<>() : new ArrayList<>(states1);
        List<User> franchises = franchises1 == null ? new ArrayList<>() : new ArrayList<>(franchises1);
        List<Stock> stocks = stocks1 == null ? new ArrayList<>() : new ArrayList<>(stocks1);
        List<Student> students = students1 == null ? new ArrayList<>() : new ArrayList<>(students1);
        List<Level> levels = levels1 == null ? new ArrayList<>() : new ArrayList<>(levels1);
        List<Book> books = books1 == null ? new ArrayList<>() : new ArrayList<>(books1);
        if (states.isEmpty()) {
            spStates.setVisibility(View.GONE);
        } else {
            stateAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, states);
            spStates.setAdapter(stateAdapter);
        }

        if (franchises.isEmpty()) {
            spFranchise.setVisibility(View.GONE);
        } else {
            franchiseAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, franchises);
            spFranchise.setAdapter(franchiseAdapter);
        }

        if (stocks.isEmpty()) {
            spStocks.setVisibility(View.GONE);
        } else {
            Stock header = new Stock();
            header.setName("Select an Item");
            stocks.add(0, header);
            stocksAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, stocks);
            spStocks.setAdapter(stocksAdapter);
        }
        if (students.isEmpty()) {
            spStudents.setVisibility(View.GONE);
        } else {
            Student header = new Student();
            header.setName("Select a Student");
            students.add(0, header);
            studentAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, students);
            spStudents.setAdapter(studentAdapter);
        }
        if (levels.isEmpty()) {
            spLevels.setVisibility(View.GONE);
        } else {
            levelAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, levels);
            spLevels.setAdapter(levelAdapter);
        }

        if (books.isEmpty()) {
            spBooks.setVisibility(View.GONE);
        } else {
            booksAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, books);
            spBooks.setAdapter(booksAdapter);
        }

        cbFilterDate.setOnCheckedChangeListener((compoundButton, b) -> {
            isDateFilterApplied = b;
            if (b) {
                llDate.setVisibility(View.VISIBLE);
                dates = getTodayDate();
                setDates();
            } else {
                llDate.setVisibility(View.GONE);
            }
        });

        if (showDateFilter) {
            cbFilterDate.setVisibility(View.VISIBLE);
        } else {
            cbFilterDate.setVisibility(View.GONE);
        }
    }

    private String[] getTodayDate() {
        return new String[]{UIUtils.getDate(), UIUtils.getDate()};
    }

    private void setDates() {
        etFromState.setText(dates[0]);
        etEndDate.setText(dates[1]);
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    public interface FilterListener {
        void onFilterCleared();

        void onFilterApplied(List<State> states, List<User> franchises, List<Stock> stocks, List<Student> students, List<Level> levels, List<Book> books, String[] dates);

        void onFilterSelected(List<String> states, List<String> franchises, List<String> stocks, List<String> students, List<String> levels, List<String> books, String[] dates);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == R.id.sp_state) {
                if (i > 0) {
                    filters[0] = (String) spStates.getAdapter().getItem(i);
                } else {
                    filters[0] = null;
                }
            } else if (adapterView.getId() == R.id.sp_franchise) {
                if (i > 0) {
                    filters[1] = ((User) spFranchise.getAdapter().getItem(i)).getId();
                } else {
                    filters[1] = null;
                }

            } else if (adapterView.getId() == R.id.sp_students) {
                if (i > 0) {
                    filters[1] = ((User) spFranchise.getAdapter().getItem(i)).getId();
                } else {
                    filters[1] = null;
                }

            }
            if (filters[0] != null || filters[1] != null) {
                btnClear.setVisibility(View.VISIBLE);
            } else {
                btnClear.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


}
