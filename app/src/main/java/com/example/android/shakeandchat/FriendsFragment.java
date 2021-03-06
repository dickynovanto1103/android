package com.example.android.shakeandchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by um on 02/20/18.
 */

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";

    private ListView mListView;
    private GoogleSignInAccount account;

    ArrayList<FriendUser> friendList;
    FriendsAdapter friendsAdapter;
    public int friendCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);
        Log.d(TAG, "onCreateView_Friends");

        mListView = view.findViewById(R.id.listFriends);
        friendList = new ArrayList<FriendUser>();
        friendsAdapter = new FriendsAdapter(getActivity(), R.layout.friends_layout, friendList);
        mListView.setAdapter(friendsAdapter);

        return view;
    }

    public void setFriendList(final GoogleSignInAccount account) {
        this.account = account;
        Log.d(TAG, "In: setFriendList");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("friendList");
        friendList = new ArrayList<FriendUser>();
        Log.d(TAG, "getReference");
        reference.child(String.valueOf(account.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendCount = (int) dataSnapshot.getChildrenCount();
                Log.d(TAG, String.valueOf(friendCount));

                for (DataSnapshot friendSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, String.valueOf(friendSnapshot));
                    String name = String.valueOf((friendSnapshot.child("name")).getValue());
                    String email = String.valueOf((friendSnapshot.child("email")).getValue());
                    String photoURL = String.valueOf((friendSnapshot.child("photoURL")).getValue());
                    friendList.add(new FriendUser(name, email, photoURL));
                }

                Log.d(TAG + "__a", String.valueOf(friendList.size()));
                Log.d(TAG + "__b", String.valueOf(friendCount));

                final FragmentActivity activity = getActivity();
                if (activity != null) {
                    friendsAdapter = new FriendsAdapter(activity, R.layout.friends_layout, friendList);
                    mListView.setAdapter(friendsAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            FriendUser item = friendsAdapter.getItem(i);
                            if (item != null) {
                                Log.d("TeSTing", String.valueOf(item));
                                Log.d("TeSTing", String.valueOf(item.getName()));
                                Intent intent = new Intent(activity, ChatActivity.class);
                                intent.putExtra("friendClicked", item);
                                intent.putExtra("Account", account);
                                startActivity(intent);
                            } else {
                                Log.d("Testing", "null bro");
                            }
                        }
                    });
                    friendsAdapter.notifyDataSetChanged();
                }

                Log.d(TAG, "done");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume_Friends");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause_Friends");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart_Friends");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop_Friends");
    }

    class FriendsAdapter extends ArrayAdapter<FriendUser> {

        ArrayList<FriendUser> userList;
        private Context context;
        private int resource;
        private View view;
        private FriendUser user;

        public FriendsAdapter(Context context, int resource, ArrayList<FriendUser> user) {
            super(context, resource, user);
            this.context = context;
            this.resource = resource;
            this.userList = user;
        }

        @Nullable
        @Override
        public FriendUser getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, viewGroup, false);

            ImageView imageFriends = view.findViewById(R.id.imageFriends);
            TextView friendsName = view.findViewById(R.id.friendsName);
            TextView friendsEmail = view.findViewById(R.id.friendsEmail);

            user = userList.get(i);

            friendsName.setText(user.name);
            friendsEmail.setText(user.email);
            if ((user.photoURL).equals("default")) {
                imageFriends.setImageResource(R.drawable.default_profile);
            } else {
                int radius = 30;
                int margin = 0;
                Glide.with(context).load(user.photoURL)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .bitmapTransform(new RoundedCornersTransformation(context, radius, margin))
                        .into(imageFriends);
            }

            return view;
        }
    }


}
