package com.meng.picTools.sauceNao;

import android.app.*;
import android.os.*;
import android.support.annotation.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.lib.MaterialDesign.*;

public class SauceNaoMain extends Fragment {
	private FloatingActionButton mFab;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.saucenao_main, container, false);
	  }

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mFab = (FloatingActionButton) view.findViewById(R.id.fab_start_download);
	  }

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFab.hide(false);
		new Handler().postDelayed(new Runnable() {
			  @Override
			  public void run() {
				  mFab.show(true);
				  mFab.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
				  mFab.setHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
				}
			}, 300);

			
			
	  }
  }
