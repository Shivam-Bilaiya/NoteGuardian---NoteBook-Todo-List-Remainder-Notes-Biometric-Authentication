package com.alphacreators.noteguardian.REMAINDERNOTES;

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
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.alphacreators.noteguardian.VIEWMODEL.SharedViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RemainderNotesBottomSheet extends BottomSheetDialogFragment {

    NoteViewModel noteViewModel;
    SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.remainder_notes_sort_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppCompatRadioButton sortNotesByDate = view.findViewById(R.id.remainder_sortNotesByDateRadioButton);
        AppCompatRadioButton sortNotesByPriority = view.findViewById(R.id.remainder_sortNotesByPriorityRadioButton);
        AppCompatRadioButton sortNotesByFavorite = view.findViewById(R.id.remainder_sortNotesByFavoriteRadioButton);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(new Application()).create(NoteViewModel.class);

        sortNotesByDate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sharedViewModel.setSortRTrigger(true);
                dismiss();
            }
        });

        sortNotesByPriority.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                sharedViewModel.setSortRPriorityTrigger(true);
                dismiss();
            }
        });

        sortNotesByFavorite.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                sharedViewModel.setSortRFavoriteTrigger(true);
                dismiss();
            }
        });
    }
}
