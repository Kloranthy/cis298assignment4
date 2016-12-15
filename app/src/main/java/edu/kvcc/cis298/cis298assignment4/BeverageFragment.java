package edu.kvcc.cis298.cis298assignment4;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * beverage detail view
 */
public class BeverageFragment extends Fragment {
    // constants
    /**
     * constant used to get contact info
     */
    private static final int REQUEST_CONTACT = 0;
    //String key that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "edu.kvcc.cis298.cis298assignment4.beverage_id";

    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;
    private CheckBox mActive;

    // view variables
    private Button mSelectContact;
    private Button mSendEmail;

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    // the name of the contact last selected
    private String contactName;
    // the email of the contact last selected
    private String contactEmail;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
        String beverageId = getArguments().getString(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get(getActivity()).getBeverage(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflater to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);
        mSelectContact = (Button) view.findViewById(R.id.select_contact_button);
        // used to retrieve a contact from contacts with emails
        final Intent selectContact = new Intent(
                Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI
        );
        mSelectContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(
                                selectContact,
                                REQUEST_CONTACT
                        );
                    }
                }
        );
        mSendEmail = (Button) view.findViewById(R.id.send_email_button);
        mSendEmail.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
//                        intent.setData(
//                                Uri.parse("mailto:")
//                        );
                        intent.putExtra(
                                Intent.EXTRA_EMAIL,
                                new String[]{
                                        contactEmail
                                }
                        );
                        // subject doesn't seem to be auto-filled on my phone
                        intent.putExtra(
                                Intent.EXTRA_SUBJECT,
                                getString(R.string.booze_report_title)
                        );
                        String stock;
                        if (mBeverage.isActive()) {
                            stock = getString(R.string.booze_report_in_stock);
                        } else {
                            stock = getString(R.string.booze_report_out_of_stock);
                        }
                        String price = "$" + mBeverage.getPrice();
                        String boozeReport = getString(
                                R.string.booze_report_body,
                                contactName,
                                mBeverage.getName(),
                                stock,
                                price,
                                mBeverage.getPack()
                        );
                        intent.putExtra(
                                Intent.EXTRA_TEXT,
                                boozeReport
                        );

                        startActivity(intent);
                    }
                }
        );
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(
                selectContact,
                packageManager.MATCH_DEFAULT_ONLY
        ) == null) {
            // disable select contact button if no contact apps are installed
            mSelectContact.setEnabled(false);
        } else {
            // disable send button until a contact is chosen
            mSendEmail.setEnabled(false);
        }

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());

        //Text changed listener for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                    //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });

        //Lastly return the view with all of this stuff attached and set on it.
        return view;
    }

    @Override
    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        if (resultCode != Activity.RESULT_OK) {
            return; // we don't deal with broken results
        }
        String contactID;
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // getting the name from the contact data
            String[] projectionName = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            try (
                    Cursor cursorName = getActivity()
                            .getContentResolver()
                            .query(
                                contactUri,// uri = the content to retrieve
                                projectionName,// projection = columns to return
                                null,// selection = sql where clause to filter results
                                null,// selection args = parameters for filter
                                null// sort order = sql order by clause
                    )
            ) {
                if (cursorName.getCount() == 0) {
                    return; // no results
                }
                cursorName.moveToFirst();
                contactID = cursorName.getString(0);
                contactName = cursorName.getString(1);
            }
            // getting the email from the contact data
            Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String[] projectionEmail = new String[] {
                    ContactsContract.CommonDataKinds.Email.DATA
            };
            String selection = ContactsContract.Contacts._ID + " = ?";
            String[] selectionArgs = new String[] {
                    contactID
            };
            try (
                    Cursor cursorEmail = getActivity()
                            .getContentResolver()
                            .query(
                                    emailUri,// uri = the content to retrieve
                                    projectionEmail,// projection = columns to return
                                    selection,// selection = sql where clause to filter results
                                    selectionArgs,// selection args = parameters for filter
                                    null// sort order = sql order by clause
                            )
            ) {
                if (cursorEmail.getCount() == 0) {
                    // no results but we don't return because
                    // we have some contact info
                    contactEmail = "";
                }
                else {
                    cursorEmail.moveToFirst();
                    contactEmail = cursorEmail.getString(
                            cursorEmail.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Email.DATA
                            )
                    );
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            // enable the email button now that we have some contact info
            mSendEmail.setEnabled(true);
        }

    }
}
