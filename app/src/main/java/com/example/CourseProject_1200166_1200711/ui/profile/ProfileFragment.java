package com.example.CourseProject_1200166_1200711.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.CourseProject_1200166_1200711.DataBaseHelper;
import com.example.CourseProject_1200166_1200711.SharedPrefManager;
import com.example.CourseProject_1200166_1200711.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPreferences;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel;// = new ViewModelProvider(this).get(ProfileViewModel.class);
        dataBaseHelper = new DataBaseHelper(requireContext(), "DB_PROJECT", null, 1);
        sharedPreferences = SharedPrefManager.getInstance(requireContext());

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ProfileViewModelFactory factory = new ProfileViewModelFactory(getActivity().getApplicationContext());
        profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> binding.profileName.setText(name));
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> binding.profileEmail.setText(email));
        profileViewModel.getPass().observe(getViewLifecycleOwner(), password -> binding.profilePass.setText(password));

        Button editButton = binding.profileEdit;
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String old_email = sharedPreferences.readString("email", null);
                String new_email = binding.profileEmail.getText().toString();
                String new_pass = binding.profilePass.getText().toString();
                dataBaseHelper.updateProfile(old_email,new_email ,new_pass);
                binding.profileEmail.setText(new_email);
                binding.profilePass.setText(new_pass);
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
