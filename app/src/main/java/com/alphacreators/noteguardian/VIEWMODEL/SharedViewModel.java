package com.alphacreators.noteguardian.VIEWMODEL;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacreators.noteguardian.ENTITY.TodoTask;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<TodoTask> selectedItems = new MutableLiveData<>();
    private boolean isEdit;

    public void selectItem(TodoTask todoTask){
        selectedItems.setValue(todoTask);
    }

    public LiveData<TodoTask> getSelectedItems(){
        return selectedItems;
    }

    public void setIsEdit(boolean isEdit){
        this.isEdit = isEdit;
    }
    public boolean getIsEdit(){
        return isEdit;
    }

    private final MutableLiveData<Boolean> sortTrigger = new MutableLiveData<>();

    public LiveData<Boolean> getSortTrigger() {
        return sortTrigger;
    }

    public void setSortTrigger(boolean trigger) {
        sortTrigger.setValue(trigger);
    }

    private final MutableLiveData<Boolean> sortPriorityTrigger = new MutableLiveData<>();

    public MutableLiveData<Boolean> getSortPriorityTrigger() {
        return sortPriorityTrigger;
    }

    public void setSortPriorityTrigger(boolean trigger) {
        sortPriorityTrigger.setValue(trigger);
    }

    private final MutableLiveData<Boolean> sortFavoriteTrigger = new MutableLiveData<>();

    public MutableLiveData<Boolean> getSortFavoriteTrigger() {
        return sortFavoriteTrigger;
    }

    public void setSortFavoriteTrigger(boolean trigger) {
        sortFavoriteTrigger.setValue(trigger);
    }

    public final MutableLiveData<Boolean> sortRTrigger = new MutableLiveData<>();

    public final MutableLiveData<Boolean> sortRPriorityTrigger = new MutableLiveData<>();

    public final MutableLiveData<Boolean> sortRFavoriteTrigger = new MutableLiveData<>();

    public LiveData<Boolean> getSortRPriorityTrigger() {
        return sortRPriorityTrigger;
    }

    public void setSortRPriorityTrigger(boolean trigger){
        sortRPriorityTrigger.setValue(trigger);
    }

    public LiveData<Boolean> getSortRFavoriteTrigger() {
        return sortRFavoriteTrigger;
    }

    public void setSortRFavoriteTrigger(boolean trigger){
        sortRFavoriteTrigger.setValue(trigger);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public LiveData<Boolean> getSortRTrigger() {
        return sortRTrigger;
    }

    public void setSortRTrigger(boolean trigger){
        sortRTrigger.setValue(trigger);
    }

}
