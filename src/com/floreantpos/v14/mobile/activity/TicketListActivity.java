package com.floreantpos.v14.mobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.adapter.TicketsAdapter;
import com.floreantpos.v14.mobile.adapter.TicketItemsAdapter;
import com.floreantpos.v14.mobile.tasks.*;
import com.floreantpos.response.TicketItemResponse;
import com.floreantpos.response.TicketResponse;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;
import com.floreantpos.v14.mobile.utils.MySharedPreference;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketListActivity extends StyledBaseFragmentActivity {

    private static TicketsAdapter ticketListAdapter;
    private static int userId;
    private static TextView emptyTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = MySharedPreference.getInteger(getApplicationContext(), "userId");

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            TicketListFragment listFragment = new TicketListFragment();
            fm.beginTransaction().add(android.R.id.content, listFragment).commit();

        }

    }


    public static class TicketListFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<List<TicketResponse>> {

        TicketChangesReceiver ticketChangesReceiver;
        private IntentFilter intentFilter;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.ticket_list, container, false);

            ticketChangesReceiver = new TicketChangesReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent);
                    refreshTicketList();
                }
            };


            ImageButton newTicketButton = (ImageButton) view.findViewById(R.id.float_button_new_ticket);

            newTicketButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTableNumberDialog();
                }
            });


            emptyTextView = (TextView) view.findViewById(android.R.id.empty);

            return view;
        }




        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);

            ticketListAdapter = new TicketsAdapter(getActivity());

            setListAdapter(ticketListAdapter);

            getLoaderManager().initLoader(0, null, this);

            intentFilter = new IntentFilter(IntentNames.TICKET_MODIFIED);

            getActivity().registerReceiver(ticketChangesReceiver, intentFilter);

            registerForContextMenu(TicketListFragment.this.getListView());

            getActivity().setTitle(getString(R.string.open_tickets));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {

            TicketResponse model = (TicketResponse) l.getItemAtPosition(position);

            goToTicketDetail(model);

        }

        void goToTicketDetail(TicketResponse ticket) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("ticketId", (ticket.id));
            intent.putExtra("tableNo", (ticket.tableNumber));

            intent.setClass(getActivity().getApplicationContext(), TicketItemsActivity.class);

            startActivity(intent);
        }


        @Override
        public Loader<List<TicketResponse>> onCreateLoader(int arg0, Bundle arg1) {
            return new TicketsLoader(getActivity());

        }

        @Override
        public void onLoadFinished(Loader<List<TicketResponse>> arg0, List<TicketResponse> data) {
            ticketListAdapter.setData(data);

        }

        @Override
        public void onLoaderReset(Loader<List<TicketResponse>> arg0) {
//            ticketListAdapter.setData(null);
        }


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }

        private void refreshTicketList() {
            getLoaderManager().restartLoader(0, null, this);
            if (ticketListAdapter != null) ticketListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onResume() {
            super.onResume();
            getLoaderManager().restartLoader(0, null, this);
            getActivity().registerReceiver(ticketChangesReceiver, intentFilter);
        }

        @Override
        public void onPause() {
            super.onPause();
            getActivity().unregisterReceiver(ticketChangesReceiver);
        }

        private void showTableNumberDialog() {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.table_no_prompt, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.table_no_ET);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    checkTableAvailability(Integer.valueOf(userInput.getText().toString()));
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }

        private void showTicketCancellationDialog(final int ticketId) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.ticket_cancellation_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            final EditText reasonET = (EditText) promptsView.findViewById(R.id.reasonEditTExt);

            alertDialogBuilder.setView(promptsView);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.YES),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    cancelTicket(ticketId,reasonET.getText().toString());
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }

        private void cancelTicket(int ticketId, String reason) {

            CancelTicketTask cancelTicketTask = new CancelTicketTask(ticketId,MySharedPreference.getInteger(getActivity(),"userId"),reason) {
                @Override
                public void onSuccess() {
                    refreshTicketList();
                    Toast.makeText(getActivity(),R.string.ticket_cancellation_succeed,Toast.LENGTH_LONG).show();
                }
                public void onFailed() {
                    Toast.makeText(getActivity(),R.string.ticket_cancellation_failed,Toast.LENGTH_LONG).show();
                }
            };

            cancelTicketTask.execute();
        }

        private void showTableMovementDialog(final int ticketId) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.table_move_prompt, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.table_no_ET);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    moveGuestToOtherTable(ticketId, Integer.valueOf(userInput.getText().toString()));
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }

        private void moveGuestToOtherTable(int ticketId, int tableNo) {


            MoveTableTask moveTableTask = new MoveTableTask(ticketId,tableNo,MySharedPreference.getInteger(getActivity(),"userId")) {
                @Override
                public void onFinished() {
                    refreshTicketList();
                    Toast.makeText(getActivity(),R.string.guest_move_succeed,Toast.LENGTH_LONG).show();
                }
            };

            moveTableTask.execute();

        }

        private void checkTableAvailability(int tableNo) {

            TableAvailabilityTask tableAvailabilityTask = new TableAvailabilityTask(tableNo) {
                @Override
                public void onAvailable(int tableNo) {

                    CreateTicketTask  createTicketTask = new CreateTicketTask(tableNo,userId) {
                        @Override
                        public void onSuccess(TicketResponse ticketResponse) {
                            goToTicketDetail(ticketResponse);
                        }

                        @Override
                        public void onFailed() {

                            Toast.makeText(getActivity(), R.string.connection_problem, Toast.LENGTH_LONG).show();

                        }
                    };

                    createTicketTask.execute();

                }

                @Override
                public void onNotAvailable(final TicketResponse ticket) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.table_is_occupied))
                            .setMessage(getString(R.string.table_is_occupied_u_want_to_edit))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    goToTicketDetail(ticket);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                }
            };

            tableAvailabilityTask.execute();
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {

            super.onCreateContextMenu(menu, v, menuInfo);

//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            MenuInflater inflater = getActivity().getMenuInflater();

            inflater.inflate(R.menu.ticket_context_menu, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            TicketResponse model = ticketListAdapter.getItem(info.position);

            switch (item.getItemId()) {

                case R.id.cancel_ticket:

                   showTicketCancellationDialog(model.id);

                    return true;

                case R.id.move_table:

                    showTableMovementDialog(model.id);

                    return true;

                default:
                    return super.onContextItemSelected(item);
            }
        }

    }


    public static class TicketItemsActivity extends StyledBaseFragmentActivity {

        private static TicketItemsAdapter ticketItemsAdapter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            FragmentManager fm = getSupportFragmentManager();

            if (fm.findFragmentById(android.R.id.content) == null) {
                TicketItemsFragment itemsFragment = new TicketItemsFragment();
                fm.beginTransaction().add(android.R.id.content, itemsFragment).commit();
            }


        }

        @Override
        public void onResume() {
            super.onResume();
            if (ticketItemsAdapter != null) ticketItemsAdapter.notifyDataSetChanged();
        }


        public static class TicketItemsFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<List<TicketItemResponse>> {

            private NewTicketItemReceiver newItemsReceiver;
            private IntentFilter itemsModifiedFilter;

            static int ticketId;
            static int tableNo;
            ListView listView;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                View view = inflater.inflate(R.layout.ticket_item_list, container, false);

                newItemsReceiver = new NewTicketItemReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //super.onReceive(context, intent);
                        refreshTicketItemList();
                    }
                };

                listView = (ListView) view.findViewById(android.R.id.list);

                ImageButton newItemButton = (ImageButton) view.findViewById(R.id.float_button_new_item);
                newItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityInvoker.showMenuCategories(getActivity(),ticketId);
                    }
                });

                ImageButton printButton = (ImageButton) view.findViewById(R.id.print_button);
                printButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmTicketTask confirmTicketTask = new ConfirmTicketTask(ticketId, userId) {
                            @Override
                            protected void onSuccess() {
                                Toast.makeText(getActivity(),R.string.ticket_print_success,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            protected void onFailed() {
                                Toast.makeText(getActivity(),R.string.ticket_print_failed,Toast.LENGTH_LONG).show();
                            }
                        };
                        confirmTicketTask.execute();
                    }
                });

                return view;
            }


            @Override
            public void onActivityCreated(Bundle savedInstanceState) {

                super.onActivityCreated(savedInstanceState);

                setHasOptionsMenu(true);

                Bundle extras = getActivity().getIntent().getExtras();

                ticketId = extras.getInt("ticketId");
                tableNo = extras.getInt("tableNo");

                ticketItemsAdapter = new TicketItemsAdapter(getActivity(),ticketId,userId);

                setListAdapter(ticketItemsAdapter);

                getLoaderManager().initLoader(0, null, this);

                itemsModifiedFilter = new IntentFilter(IntentNames.TICKET_ITEMS_MODIFIED);

                getActivity().registerReceiver(newItemsReceiver, itemsModifiedFilter);

                registerForContextMenu(getListView());

                getActivity().setTitle(getString(R.string.table) + " " + tableNo);
            }


            @Override
            public Loader<List<TicketItemResponse>> onCreateLoader(int arg0, Bundle arg1) {
                return new TicketItemsLoader(ticketId,getActivity());
            }

            @Override
            public void onLoadFinished(Loader<List<TicketItemResponse>> arg0, List<TicketItemResponse> data) {
                ticketItemsAdapter.setData(data);
            }

            @Override
            public void onLoaderReset(Loader<List<TicketItemResponse>> arg0) {
                ticketItemsAdapter.setData(null);
            }


            @Override
            public void onViewCreated(View view, Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
            }

            private void refreshTicketItemList() {
                getLoaderManager().restartLoader(0, null, this);
                if (ticketItemsAdapter != null) ticketItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResume() {
                super.onResume();
                getLoaderManager().restartLoader(0, null, this);
                getActivity().registerReceiver(newItemsReceiver, itemsModifiedFilter);
            }

            @Override
            public void onPause() {
                super.onPause();
                getActivity().unregisterReceiver(newItemsReceiver);
            }

            private void showCookingInstructionDialog(final int ticketId, final int ticketItemId) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.cooking_instruction_prompt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.instruction_ET);

                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                AddCookingInstructionTask task = new AddCookingInstructionTask(ticketId, ticketItemId, userInput.getText().toString(), MySharedPreference.getInteger(getActivity(),"userId")) {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getActivity(), R.string.cooking_instruction_saved, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailed() {
                                        Toast.makeText(getActivity(), R.string.cooking_instruction_saving_failed, Toast.LENGTH_LONG).show();
                                    }
                                };

                                task.execute();

                            }
                        });
                alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                super.onCreateContextMenu(menu, v, menuInfo);

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

                TicketItemResponse model = ticketItemsAdapter.getItem(info.position);

                MenuInflater inflater = getActivity().getMenuInflater();

                inflater.inflate(R.menu.ticket_items_context_menu, menu);

                if (!model.canAddCookingInstruction) {
                    MenuItem addInstruction = menu.findItem(R.id.add_instruction);
                    addInstruction.setVisible(false);
                }
            }

            @Override
            public boolean onContextItemSelected(MenuItem item) {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                TicketItemResponse model = ticketItemsAdapter.getItem(info.position);

                switch (item.getItemId()) {

                    case R.id.drop_item:

                        new RemoveItemFromTicketTask(model.ticketId,model.id,MySharedPreference.getInteger(getActivity(),"userId")){
                            @Override
                            public void onSuccess() {
                                refreshTicketItemList();
                                ticketItemsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(getActivity(),R.string.connection_problem,Toast.LENGTH_LONG).show();
                            }
                        }.execute();

                        return true;

                    case R.id.add_instruction:

                        showCookingInstructionDialog(model.ticketId,model.id);

                        return true;

                    default:
                        return super.onContextItemSelected(item);
                }
            }
        }

        public static class TicketItemsLoader extends AsyncTaskLoader<List<TicketItemResponse>> {

            List<TicketItemResponse> ticketItemList;
            int ticketId;

            public TicketItemsLoader(int ticketId, Context context) {
                super(context);
                this.ticketId = ticketId;
            }

            @Override
            public List<TicketItemResponse> loadInBackground() {

                List<TicketItemResponse> result = null;

                String uri = GV.URL + "/ViewTicketDetailServlet";

                try {

                    URL url = new URL(uri);
                    CookieManager cookieManager = new CookieManager();
                    CookieHandler.setDefault(cookieManager);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("ticketId", String.valueOf(ticketId)));

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(params));
                    writer.flush();
                    writer.close();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder temp = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        temp.append((inputLine));
                    }

                    in.close();

                    TicketResponse serverResponse = new Gson().fromJson(temp.toString(),TicketResponse.class);

                    if (serverResponse!=null) {
                        result = serverResponse.ticketItems;
                    }

                } catch (Exception e) {
                    Log.d("FloreantPOS", e.getMessage());
                }

                return result;

            }


            @Override
            public void deliverResult(List<TicketItemResponse> listOfData) {
                ticketItemList = listOfData;

                if (isStarted()) {
                    super.deliverResult(listOfData);
                }

            }

            @Override
            protected void onStartLoading() {
                if (ticketItemList != null) {
                    deliverResult(ticketItemList);
                }


                if (takeContentChanged() || ticketItemList == null) {
                    forceLoad();
                }
            }

            @Override
            protected void onStopLoading() {
                cancelLoad();
            }

            @Override
            protected void onReset() {
                super.onReset();

                onStopLoading();

                if (ticketItemList != null) {
                    ticketItemList = null;
                }
            }


            private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
                StringBuilder result = new StringBuilder();
                boolean first = true;

                for (NameValuePair pair : params) {
                    if (first)
                        first = false;
                    else
                        result.append("&");

                    result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
                }

                return result.toString();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            super.onCreateOptionsMenu(menu);

            MenuInflater inflater = getMenuInflater();

            inflater.inflate(R.menu.activity_main_actions, menu);

            MenuItem homeMenu = menu.findItem(R.id.homeMenu);
            homeMenu.setVisible(false);

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.homeMenu:
                    ActivityInvoker.goToOpenTickets(getApplicationContext());
                    return true;
                case R.id.logoutMenu:
                    ActivityInvoker.logout(getApplicationContext());
                    return true;
                case R.id.clockOutMenu:
                    ActivityInvoker.clockOut(getApplicationContext());
                    return true;
                case R.id.settingMenu:
                    ActivityInvoker.showSettings(getApplicationContext());
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    }


    @Override
    public void onResume() {

        super.onResume();

        if (ticketListAdapter != null) ticketListAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.activity_main_actions, menu);

        MenuItem homeMenu = menu.findItem(R.id.homeMenu);
        homeMenu.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.homeMenu:
                ActivityInvoker.goToOpenTickets(getApplicationContext());
                return true;
            case R.id.logoutMenu:
                ActivityInvoker.logout(getApplicationContext());
                return true;
            case R.id.clockOutMenu:
                ActivityInvoker.clockOut(getApplicationContext());
                return true;
            case R.id.settingMenu:
                ActivityInvoker.showSettings(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class TicketsLoader extends AsyncTaskLoader<List<TicketResponse>> {

        List<TicketResponse> mModels;

        public TicketsLoader(Context context) {
            super(context);
        }

        @Override
        public List<TicketResponse> loadInBackground() {

            List<TicketResponse> result = null;

            String uri = GV.URL + "/FetchOpenTicketsServlet";

            try {

                URL url = new URL(uri);
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder temp = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    temp.append((inputLine));
                }

                in.close();

                TicketResponse[] array = new Gson().fromJson(temp.toString(),TicketResponse[].class);

                if (array==null) {
                    result = new ArrayList<TicketResponse>();
                } else {
                    result = Arrays.asList(array);
                }

            } catch (Exception e) {
                Log.d("FloreantPOS", e.getMessage());
            }

            return result;

        }


        @Override
        public void deliverResult(List<TicketResponse> listOfData) {
            mModels = listOfData;

            if (isStarted()) {
                super.deliverResult(listOfData);
            }

            if (listOfData!=null && listOfData.isEmpty()) {
                if (emptyTextView!=null) emptyTextView.setText(R.string.empty_ticket_item);
            }

        }

        @Override
        protected void onStartLoading() {
            if (mModels != null) {
                deliverResult(mModels);
            }


            if (takeContentChanged() || mModels == null) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        public void onCanceled(List<TicketResponse> apps) {
            super.onCanceled(apps);
        }

        @Override
        protected void onReset() {
            super.onReset();

            onStopLoading();

            if (mModels != null) {
                mModels = null;
            }
        }
    }

}
