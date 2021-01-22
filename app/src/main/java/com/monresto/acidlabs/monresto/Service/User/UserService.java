package com.monresto.acidlabs.monresto.Service.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.Order;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.Utilities;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.monresto.acidlabs.monresto.Model.Semsem.loginPending;

public class UserService {
    private static final int TIMEOUT_MILLISECNDS = 30000;
    private static final String TAG = "DTSE_UserService";//todo add log
    private Context context;
    private Fragment fragment;

    public UserService(Context context) {
        this.context = context;
    }

    public UserService(Context context, Fragment fragment) {
        this.fragment = fragment;
        this.context = context;
    }

    public void checkLoginDispo(final String login, final String email) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/disponibilite.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            boolean isDispo = true;
                            if (status != 1)
                                isDispo = false;
                            ((UserAsyncResponse) context).oncheckLoginDispoReceived(isDispo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String signature = Utilities.md5(email + login + Config.sharedKey);
                params.put("login", login);
                params.put("email", email);
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void register(final String login, final String password, final String password_confirm, final String email, final String fname, final String lname,
                         final String civility, final String phone, final String mobile, final String comment, final ArrayList<Address> addresses) {
        final JSONArray addressesArray = new JSONArray();
        for (Address A : addresses) {
            addressesArray.put(A.toJson());
        }


        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/Register.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject clientObject = jsonResponse.getJSONObject("Client");
                            int status = Integer.parseInt(jsonResponse.getString("Status"));
                            if (status != 0) {
                                int id = clientObject.optInt("userID");
                                User user = new User(id, login, email, fname, lname, civility, phone, mobile, comment, addresses);
                                ((UserAsyncResponse) context).onUserLogin(user);
                            }


                        } catch (JSONException e) {


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject userObject = User.registerJson(login, password, password_confirm, email, fname, lname, civility, phone, mobile, comment, addresses);
                String token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getPushToken();
                //todo correct token issue {
                if (token == null) token = "";
                //}
                String signature = Utilities.md5(userObject.toString() + token + "android" + Config.sharedKey);

                params.put("user", userObject.toString());
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    /**
     * @param login    user login
     * @param password user password
     *                 <p>
     *                 onUserLogin should call for getDetails with login parameter true
     */
    public void login(final String login, final String password, SharedPreferences sharedPref) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/Login.php";
        loginPending = true;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status != 0) {
                                JSONObject clientObject = jsonResponse.getJSONObject("Client");
                                int id = clientObject.optInt("userID");
                                User user = new User(id, login, "", "", "", "", "", "", "", null);
                                String isLogin = sharedPref.getString("passwordLogin", null);
                                if (isLogin == null) {
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("passwordLogin", "{login: " + login + ", password: " + password + "}");
                                    editor.apply();
                                }
                                ((UserAsyncResponse) context).onUserLogin(user);
                            } else {
                                ((UserAsyncResponse) context).onUserLogin(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                //todo correct token issue {
                if (token == null) token = "";
                //}
                String signature = Utilities.md5(login + password + token + "android" + Config.sharedKey);
                params.put("login", login);
                params.put("password", password);
                params.put("token", token);
                params.put("device", "android");
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }


    public void forget(final String login) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/forgotPassword.php";
        loginPending = true;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status != 0) {

                                ((UserAsyncResponse) context).onForgetSuccess("Demande de mot passe effectuée, verifier votre boite mail");
                            } else {
                                ((UserAsyncResponse) context).onForgetSuccess("Une érreur s'est produite, vérifiez votre email");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ((UserAsyncResponse) context).onForgetSuccess("Une érreur s'est produite");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((UserAsyncResponse) context).onForgetSuccess("Une érreur s'est produite");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                //todo correct token issue{
                if (token == null) token = "";
                //}
                String signature = Utilities.md5(login + Config.sharedKey);
                params.put("email", login);
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void updateProfile(User user) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/editUser.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            if (responseJson.getInt("Status") == 1) {
                                User.setInstance(user);
                                ((UserAsyncResponse) context).onUserProfileUpdated();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject userObject = User.profileJson(user);
                String signature = Utilities.md5(userObject.toString() + Config.sharedKey);

                params.put("user", userObject.toString());
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void facebookLogin(final String socialID, final String email, final String fname, final String lname, SharedPreferences sharedPref) {
        final String socialNetworkID = "fb";
        final String deviceID = "android";
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/socialConnect.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "facebookLogin onResponse response: " + response);//todo add log
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject clientObject = jsonResponse.getJSONObject("Client");
                            int status = jsonResponse.getInt("Status");
                            if (status != 0) {
                                int id = clientObject.optInt("userID");
                                String mobile = clientObject.getString("mobile");

                                User user = new User(id, email, fname, lname, mobile);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("fbLogin", "{id: " + id + ", email: " + email + ", fname: " + fname + ", lname: " + lname + ", mobile: " + mobile + "}");
                                editor.apply();
                                ((UserAsyncResponse) context).onUserLogin(user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "facebookLogin JSONException: " + e.getMessage());//todo add log
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.i(TAG, "facebookLogin onErrorResponse : " + error.getMessage());//todo add log
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getPushToken();
                //todo correct token issue {
                if (token == null) token = "";
                //}
                String signature = Utilities.md5(socialID + email + fname + lname + socialNetworkID + token + deviceID + "vZ!m@73@tH*c2jPV4Z2");
                params.put("social_id", socialID);
                params.put("email", email);
                params.put("first_name", fname);
                params.put("last_name", lname);
                params.put("type", String.valueOf(socialNetworkID));
                params.put("token", token);
                params.put("device_id", String.valueOf(deviceID));
                params.put("signature", signature);
                Log.i(TAG, "facebookLogin getParams : " + socialID + "-$-" + email + "-$-" + fname + "-$-" + lname + "-$-" + socialNetworkID + "-$-" + token + "-$-" + deviceID);////todo add log
                return params;
            }
        };
        postRequest.setShouldCache(false);
        queue.add(postRequest);


    }

    /**
     * @param id    user's id
     * @param login is true when user details are requested after login
     */
    public void getDetails(final int id, final boolean login) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/userDetails.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status != 0) {
                                JSONObject clientObject = jsonResponse.optJSONObject("User");
                                User user = new User(id, clientObject.optString("login"), clientObject.optString("email"), clientObject.optString("firstName"), clientObject.optString("lastName"), clientObject.optString("civility"), clientObject.optString("phone"), clientObject.optString("mobile"), "", null);
                                User.setInstance(user);
                                ((UserAsyncResponse) context).onUserDetailsReceived(user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String signature = Utilities.md5(id + Config.sharedKey);
                params.put("userID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getAddress(final int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/address.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = jsonResponse.getJSONArray("Address");
                            ArrayList<Address> addresses = Address.makeListFromJson(array);
                            ((UserAsyncResponse) context).onAddressListReceived(addresses);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String signature = Utilities.md5(id + Config.sharedKey);
                params.put("userID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void addAddress(final Address address) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Address/addAddress.php";
        int userID = 0;
        if (User.getInstance() != null) {
            userID = User.getInstance().getId();//49468
        }
        int finalUserID = userID;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            ((UserAsyncResponse) (context)).onAddressAddResponse(status == 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonAddress = address.toJson();
                try {
                    jsonAddress.put("userID", finalUserID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String signature = Utilities.md5(jsonAddress.toString() + Config.sharedKey);

                params.put("Address", jsonAddress.toString());
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void editAddress(final Address address) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Address/editAddress.php";
        final int userID = User.getInstance().getId();//49468
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                          /*  if (status == 1) {
                                UserService userService = new UserService(context);
                                userService.getAddress(userID);
                            }*/
                            ((UserAsyncResponse) (context)).onAddressAddResponse(status == 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonAddress = address.toJson();
                try {
                    jsonAddress.put("userID", userID);
                    jsonAddress.put("adressID", address.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String signature = Utilities.md5(jsonAddress.toString() + Config.sharedKey);

                params.put("Address", jsonAddress.toString());
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void deleteAddress(final int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Address/deleteAddress.php";
        final int userID = User.getInstance().getId();//49468

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status == 1) {
                                UserService userService = new UserService(context);
                                userService.getAddress(userID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String signature = Utilities.md5(String.valueOf(id) + String.valueOf(userID) + Config.sharedKey);

                params.put("signature", signature);
                params.put("adressID", String.valueOf(id));
                params.put("userID", String.valueOf(userID));

                return params;
            }
        };
        queue.add(postRequest);


    }

    /**
     * Modify user info
     */
    public void modifyInfo(User user) {
        final JSONArray addressesArray = new JSONArray();
        /*for (Address A : addresses) {
            addressesArray.put(A.toJson());
        }*/

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/Register.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject clientObject = jsonResponse.getJSONObject("Client");
                            int status = clientObject.getInt("Status");
                            if (status != 0) {
                                int id = clientObject.optInt("userID");
                                //((UserAsyncResponse) context).onUserLogin(user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                /*JSONObject userObject = User.registerJson(login, password, password_confirm, fname, lname, civility, email, phone, mobile, comment, addresses);
                String token = "kento";//"token";
                String signature = Utilities.md5(userObject.toString() + token + "android" + Config.sharedKey);

                params.put("user", userObject.toString());
                params.put("signature", signature);*/

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void cancelOrder(final int userID, final Order order) {
        final String signature = Utilities.md5("" + order.getOrderID() + userID + Config.sharedKey);
        RequestQueue queue = Volley.newRequestQueue(context);
        System.out.println("order sig = [" + signature + "]");

        String url = Config.server + "User/cancelOrder.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        System.out.println("order response = [" + response + "]");
                        JSONObject responseJson = new JSONObject(response);
                        if (responseJson.getInt("Status") == 1) {
                            ((UserAsyncResponse) fragment).onCancelOrder(true);
                        } else
                            ((UserAsyncResponse) fragment).onCancelOrder(false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("clientID", String.valueOf(userID));
                params.put("orderID", String.valueOf(order.getOrderID()));
                params.put("signature", signature);

                System.out.println("Params: " + params);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void submitOrders(final int userID, final int type, final int addressID, final int restoID, final String promo, final int paymentID, final int optionOrderID, final int deliveryTime, final String hour, final JSONArray orders) {
        final JSONArray voucher = new JSONArray();
        final int numtrans = 0;
        final String time = String.valueOf(deliveryTime);

        final String signature = Utilities.md5("" + userID + addressID + restoID + orders.toString() + paymentID + type + "[]" + numtrans + optionOrderID + time + hour + promo + Config.sharedKey);

        RequestQueue queue = Volley.newRequestQueue(context);
        System.out.println("order sig = [" + signature + "]");

        String url = Config.server + "User/purchaseOrder.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("order response = [" + response + "]");
                            JSONObject responseJson = new JSONObject(response);
                            if (responseJson.getInt("Status") == 1) {
                                if (paymentID == 1) {
                                    int orderID = responseJson.getInt("orderID");
                                    ((UserAsyncResponse) context).onSubmitOrder(true, orderID);

                                } else {
                                    ((UserAsyncResponse) context).onSubmitOrder(true);
                                }
                            } else
                                ((UserAsyncResponse) context).onSubmitOrder(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    // error
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("userID", String.valueOf(userID));
                params.put("addressID", String.valueOf(addressID));
                params.put("restoID", String.valueOf(restoID));
                params.put("orders", orders.toString());
                params.put("paymentID", String.valueOf(paymentID));
                params.put("type", String.valueOf(type));
                params.put("numtrans", String.valueOf(numtrans));
                params.put("Voucher", "[]");
                params.put("promo", promo);
                params.put("optionOrderID", String.valueOf(optionOrderID));
                params.put("time", String.valueOf(time));
                params.put("hour", String.valueOf(hour));
                params.put("signature", signature);

                System.out.println("Params: " + params);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MILLISECNDS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);
    }

    //Order details
    public void getHistory(final int id, final int restoID) {
        //int _id = 49468;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/orderHistory.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("history = [" + response + "]");
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = jsonResponse.getJSONArray("Order");
                            ArrayList<Order> orders = Order.makeListFromJson(array);

                            ((UserAsyncResponse) context).onHistoryReceived(orders);

                            //((UserAsyncResponse) context).onHistoryReceived();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5(id + Config.sharedKey);
                params.put("userID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getOrders(final int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/pendingOrderV2.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = jsonResponse.getJSONArray("Order");
                            ArrayList<Order> orders = Order.makeListFromJson(array);
                            ((UserAsyncResponse) context).onPendingReceived(orders);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5(id + Config.sharedKey);
                params.put("userID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getFavoritesDishes(final int id) {
        ArrayList<Dish> dishesList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/favoritesDish.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonMenus = jsonResponse.getJSONArray("Dishes");
                            JSONObject obj;
                            for (int i = 0; i < jsonMenus.length(); i++) {
                                obj = jsonMenus.getJSONObject(i);
                                dishesList.add(Dish.createFromJson(obj));
                            }
                            ((UserAsyncResponse) context).onFavoriteDishesReceived(dishesList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5(id + Config.sharedKey);
                params.put("userID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }


    public void logout() {

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                SharedPreferences sharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
            }
        }).executeAsync();

        User.setInstance(null);
    }


}
