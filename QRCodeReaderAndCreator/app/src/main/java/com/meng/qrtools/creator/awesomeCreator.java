package com.meng.qrtools.creator;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sumimakito.awesomeqr.AwesomeQRCode;
import com.meng.qrtools.R;
import android.view.*;

public class awesomeCreator extends Fragment {

	private final int SELECT_FILE_REQUEST_CODE = 822;

	private ImageView qrCodeImageView;
	private EditText etColorLight, etColorDark, etContents, etMargin, etSize;
	private Button btGenerate, btSelectBG, btRemoveBackgroundImage;
	private CheckBox ckbWhiteMargin;
	private Bitmap backgroundImage = null;
	private AlertDialog progressDialog;
	private boolean generating = false;
	private CheckBox ckbAutoColor;
	private TextView tvAuthorHint;
	private ScrollView scrollView;
	private EditText etDotScale;
	private TextView tvJSHint;
	private CheckBox ckbBinarize;
	private EditText etBinarizeThreshold;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.awesomeqr_main, container, false);
	//	return super.onCreateView(inflater,container,savedInstanceState);
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
		

		scrollView = (ScrollView)view. findViewById(R.id.scrollView);
		tvAuthorHint = (TextView)view. findViewById(R.id.authorHint);
		tvJSHint = (TextView)view. findViewById(R.id.jsHint);
		qrCodeImageView = (ImageView)view. findViewById(R.id.qrcode);
		etColorLight = (EditText)view. findViewById(R.id.colorLight);
		etColorDark = (EditText)view. findViewById(R.id.colorDark);
		etContents = (EditText)view. findViewById(R.id.contents);
		etSize = (EditText)view. findViewById(R.id.size);
		etMargin = (EditText)view. findViewById(R.id.margin);
		etDotScale = (EditText)view. findViewById(R.id.dotScale);
		btSelectBG = (Button)view. findViewById(R.id.backgroundImage);
		btRemoveBackgroundImage = (Button)view. findViewById(R.id.removeBackgroundImage);
		btGenerate = (Button)view. findViewById(R.id.generate);
		ckbWhiteMargin = (CheckBox)view. findViewById(R.id.whiteMargin);
		ckbAutoColor = (CheckBox)view. findViewById(R.id.autoColor);
		ckbBinarize= (CheckBox)view. findViewById(R.id.binarize);
		etBinarizeThreshold = (EditText)view. findViewById(R.id.binarizeThreshold);

		ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					etColorLight.setEnabled(!isChecked);
					etColorDark.setEnabled(!isChecked);
				}
			});

		ckbBinarize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					etBinarizeThreshold.setEnabled(isChecked);
				}
			});

		btSelectBG.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
				}
			});

		btRemoveBackgroundImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					backgroundImage = null;
					Toast.makeText(getActivity().getApplicationContext(), "Background image removed.", Toast.LENGTH_SHORT).show();
				}
			});

		btGenerate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {

						generate(etContents.getText().length() == 0 ? "Makito loves Kafuu Chino." : etContents.getText().toString(),
								 etSize.getText().length() == 0 ? 800 : Integer.parseInt(etSize.getText().toString()),
								 etMargin.getText().length() == 0 ? 20 : Integer.parseInt(etMargin.getText().toString()),
								 etDotScale.getText().length() == 0 ? 0.3f : Float.parseFloat(etDotScale.getText().toString()),
								 ckbAutoColor.isChecked() ? Color.BLACK : Color.parseColor(etColorDark.getText().toString()),
								 ckbAutoColor.isChecked() ? Color.WHITE : Color.parseColor(etColorLight.getText().toString()),
								 backgroundImage,
								 ckbWhiteMargin.isChecked(),
								 ckbAutoColor.isChecked(),
								 ckbBinarize.isChecked(),
								 etBinarizeThreshold.getText().length() == 0 ? 128 : Integer.parseInt(etBinarizeThreshold.getText().toString())
								 );
					} catch (Exception e) {
						Toast.makeText(getActivity().getApplicationContext(),  "Error occurred, please check your configs.", Toast.LENGTH_LONG).show();
					}
				}
			});

		tvAuthorHint.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = "https://github.com/SumiMakito/AwesomeQRCode";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});

		tvJSHint.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = "https://github.com/SumiMakito/Awesome-qr.js";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
	}

	
/*		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awesomeqr_main);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		tvAuthorHint = (TextView) findViewById(R.id.authorHint);
		tvJSHint = (TextView) findViewById(R.id.jsHint);
		qrCodeImageView = (ImageView) findViewById(R.id.qrcode);
		etColorLight = (EditText) findViewById(R.id.colorLight);
		etColorDark = (EditText) findViewById(R.id.colorDark);
		etContents = (EditText) findViewById(R.id.contents);
		etSize = (EditText) findViewById(R.id.size);
		etMargin = (EditText) findViewById(R.id.margin);
		etDotScale = (EditText) findViewById(R.id.dotScale);
		btSelectBG = (Button) findViewById(R.id.backgroundImage);
		btRemoveBackgroundImage = (Button) findViewById(R.id.removeBackgroundImage);
		btGenerate = (Button) findViewById(R.id.generate);
		ckbWhiteMargin = (CheckBox) findViewById(R.id.whiteMargin);
		ckbAutoColor = (CheckBox) findViewById(R.id.autoColor);
		ckbBinarize= (CheckBox) findViewById(R.id.binarize);
		etBinarizeThreshold = (EditText) findViewById(R.id.binarizeThreshold);

		ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				etColorLight.setEnabled(!isChecked);
				etColorDark.setEnabled(!isChecked);
			}
		});

		ckbBinarize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				etBinarizeThreshold.setEnabled(isChecked);
			}
		});

		btSelectBG.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
			}
		});

		btRemoveBackgroundImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backgroundImage = null;
				Toast.makeText(awesomeCreator.this, "Background image removed.", Toast.LENGTH_SHORT).show();
			}
		});

		btGenerate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					generate(etContents.getText().length() == 0 ? "Makito loves Kafuu Chino." : etContents.getText().toString(),
							etSize.getText().length() == 0 ? 800 : Integer.parseInt(etSize.getText().toString()),
							etMargin.getText().length() == 0 ? 20 : Integer.parseInt(etMargin.getText().toString()),
							etDotScale.getText().length() == 0 ? 0.3f : Float.parseFloat(etDotScale.getText().toString()),
							ckbAutoColor.isChecked() ? Color.BLACK : Color.parseColor(etColorDark.getText().toString()),
							ckbAutoColor.isChecked() ? Color.WHITE : Color.parseColor(etColorLight.getText().toString()),
							backgroundImage,
							ckbWhiteMargin.isChecked(),
							ckbAutoColor.isChecked(),
							ckbBinarize.isChecked(),
							etBinarizeThreshold.getText().length() == 0 ? 128 : Integer.parseInt(etBinarizeThreshold.getText().toString())
					);
				} catch (Exception e) {
					Toast.makeText(awesomeCreator.this, "Error occurred, please check your configs.", Toast.LENGTH_LONG).show();
				}
			}
		});

		tvAuthorHint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "https://github.com/SumiMakito/AwesomeQRCode";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		tvJSHint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "https://github.com/SumiMakito/Awesome-qr.js";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		}); 
	}
*/
	@Override
	public void onResume() {
		super.onResume();
		acquireStoragePermissions();
	}

	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	private void acquireStoragePermissions() {
		int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
				getActivity(),
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode ==getActivity().RESULT_OK && data.getData() != null) {
			try {
				Uri imageUri = data.getData();
				backgroundImage = BitmapFactory.decodeFile(ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), imageUri));
				Toast.makeText(getActivity().getApplicationContext(),  "Background image added.", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity().getApplicationContext(), "Failed to add the background image.", Toast.LENGTH_SHORT).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void generate(final String contents, final int size, final int margin, final float dotScale,
						  final int colorDark, final int colorLight, final Bitmap background, final boolean whiteMargin,
						  final boolean autoColor, final boolean binarize, final int binarizeThreshold) {
		if (generating) return;
		generating = true;
		progressDialog = new ProgressDialog.Builder(getActivity().getApplicationContext()).setMessage("Generating...").setCancelable(false).create();
		progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final Bitmap b = AwesomeQRCode.create(contents, size, margin, dotScale, colorDark, colorLight, background, whiteMargin, autoColor, binarize, binarizeThreshold);
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							qrCodeImageView.setImageBitmap(b);
							scrollView.post(new Runnable() {
								@Override
								public void run() {
									scrollView.fullScroll(View.FOCUS_DOWN);
								}
							});
							if (progressDialog != null) progressDialog.dismiss();
							generating = false;
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					getActivity(). runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (progressDialog != null) progressDialog.dismiss();
							generating = false;
						}
					});
				}
			}
		}).start();
	}
}
