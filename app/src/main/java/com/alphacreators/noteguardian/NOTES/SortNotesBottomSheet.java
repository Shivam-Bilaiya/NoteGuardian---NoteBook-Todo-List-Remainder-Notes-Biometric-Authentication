package com.alphacreators.noteguardian.NOTES;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.lifecycle.ViewModelProvider;

import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.VIEWMODEL.SharedViewModel;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SortNotesBottomSheet extends BottomSheetDialogFragment {

    NoteViewModel noteViewModel;
    SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sort_notes_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatRadioButton sortNotesByDate = view.findViewById(R.id.sortNotesByDateRadioButton);
        AppCompatRadioButton sortNotesByPriority = view.findViewById(R.id.sortNotesByPriorityRadioButton);
        AppCompatRadioButton sortNotesByFavorite = view.findViewById(R.id.sortNotesByFavoriteRadioButton);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(new Application()).create(NoteViewModel.class);

        sortNotesByDate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sharedViewModel.setSortTrigger(true);
                dismiss();
            }
        });

        sortNotesByPriority.setOnCheckedChangeListener((compoundButton, b) -> {
             if (b){
                 sharedViewModel.setSortPriorityTrigger(true);
                 dismiss();
             }
        });

        sortNotesByFavorite.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                sharedViewModel.setSortFavoriteTrigger(true);
                dismiss();
            }
        });



    }


}
