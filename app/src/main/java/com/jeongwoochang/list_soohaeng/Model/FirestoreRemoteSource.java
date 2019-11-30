package com.jeongwoochang.list_soohaeng.Model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Content;
import com.jeongwoochang.list_soohaeng.Model.Schema.Log;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.Model.Schema.User;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

public class FirestoreRemoteSource {

    public static final String PUB_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String TEST_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ALARM_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String LOG_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private final static String LOG_TAG = FirestoreRemoteSource.class.getSimpleName();

    //User Table
    public static final String TABLE_USER = "user";
    public static final String USER_UID = "uid";
    public static final String USER_EMAIL = "email";

    //Test Group Table
    public static final String TABLE_TEST_GROUP = "test_group";
    public static final String TEST_GROUP_ID = "_id";
    public static final String TEST_GROUP_NAME = "name";
    public static final String TEST_GROUP_OWNER = "owner";
    public static final String TEST_GROUP_MEMBERS = "members";
    public static final String TEST_GROUP_PUB_DATE = "pub_date";

    //Test Table
    public static final String TABLE_TEST = "test";
    public static final String TEST_ID = "_id";
    public static final String TEST_GROUP = "test_group";
    public static final String TEST_NAME = "name";
    public static final String TEST_SUBJECT = "subject";
    public static final String TEST_DATE = "date";
    public static final String TEST_CONTENT = "content";
    public static final String CONTENT_FILE_NAME = "content_file_name";
    public static final String CONTENT_EXTENSION = "content_extension";
    public static final String CONTENT_CONTENT = "content_content";
    public static final String TEST_EXPECTED_TIME = "expected_time";
    public static final String TEST_PUB_DATE = "pub_date";

    //Log Table
    public static final String TABLE_LOG = "log";
    public static final String LOG_ID = "_id";
    public static final String LOG_USER = "user";
    public static final String LOG_TEST = "test";
    public static final String LOG_IS_UPDATE = "is_update";
    public static final String LOG_DATE = "date";

    private FirebaseFirestore db;
    private CollectionReference testsRef;
    private CollectionReference testGroupsRef;
    private CollectionReference logsRef;
    private CollectionReference userssRef;

    private static FirestoreRemoteSource instance;

    private FirestoreRemoteSource() {
    }

    public synchronized static FirestoreRemoteSource getInstance() {
        if (instance == null) {
            instance = new FirestoreRemoteSource();
            instance.db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            instance.db.setFirestoreSettings(settings);
            instance.testsRef = instance.db.collection("tests");
            instance.testGroupsRef = instance.db.collection("testGroups");
            instance.logsRef = instance.db.collection("logs");
            instance.userssRef = instance.db.collection("users");
        }
        return instance;
    }

    public void addUser(User user, OnCompleteListener<User> onCompleteListener) {
        userssRef.whereEqualTo(USER_UID, user.getUid()).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Map<String, Object> u = new HashMap<>();
                        u.put(USER_UID, user.getUid());
                        u.put(USER_EMAIL, user.getEmail());
                        userssRef.add(u).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    if (onCompleteListener != null)
                                        onCompleteListener.onComplete(user);
                                } else {
                                    if (onCompleteListener != null)
                                        onCompleteListener.onException(task.getException());
                                }
                            }
                        });
                    }
                } else {
                    onCompleteListener.onException(task.getException());
                }
            }
        });
    }

    public void searchUserByEmail(String email, OnCompleteListener<ArrayList<User>> onCompleteListener) {
        userssRef.orderBy(USER_EMAIL).startAt(email).endAt(email + '\uf8ff').get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<User> result = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map u = document.getData();
                        result.add(new User((String) u.get(USER_UID), (String) u.get(USER_EMAIL)));
                    }
                    if (onCompleteListener != null)
                        onCompleteListener.onComplete(result);
                } else {
                    if (onCompleteListener != null)
                        onCompleteListener.onException(task.getException());
                }
            }
        });
    }

    /**
     * @param testGroup TestGroup
     * @brief 수행평가 그룹 추가합니다.
     */
    public void addTestGroup(@Nullable TestGroup testGroup, OnCompleteListener<TestGroup> onCompleteListener) {
        if (testGroup != null) {
            Map<String, Object> tg = new HashMap();
            tg.put(TEST_GROUP_NAME, testGroup.getName());
            tg.put(TEST_GROUP_PUB_DATE, testGroup.getPubDateString(PUB_DATE_FORMAT));

            Map<String, Object> owner = new HashMap<>();
            owner.put(USER_UID, testGroup.getOwner().getUid());
            owner.put(USER_EMAIL, testGroup.getOwner().getEmail());
            tg.put(TEST_GROUP_OWNER, owner);

            ArrayList<Map<String, Object>> members = new ArrayList<>();
            for (User m : testGroup.getMembers()) {
                Map<String, Object> member = new HashMap<>();
                member.put(USER_UID, m.getUid());
                member.put(USER_EMAIL, m.getEmail());
                members.add(member);
            }
            tg.put(TEST_GROUP_MEMBERS, members);

            testGroupsRef
                    .add(tg)
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                testGroup.set_id(task.getResult().getId());
                                if (onCompleteListener != null)
                                    onCompleteListener.onComplete(testGroup);
                            } else {
                                if (onCompleteListener != null)
                                    onCompleteListener.onException(task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * @param testGroup          TestGroup
     * @param onCompleteListener
     * @brief 수행평가를 추가합니다.
     */
    public void updateTestGroup(@Nullable TestGroup testGroup, OnCompleteListener<TestGroup> onCompleteListener) {
        if (testGroup != null) {
            Map<String, Object> tg = new HashMap();
            tg.put(TEST_GROUP_NAME, testGroup.getName());
            tg.put(TEST_GROUP_PUB_DATE, testGroup.getPubDateString(PUB_DATE_FORMAT));

            Map<String, Object> owner = new HashMap<>();
            owner.put(USER_UID, testGroup.getOwner().getUid());
            owner.put(USER_EMAIL, testGroup.getOwner().getEmail());
            tg.put(TEST_GROUP_OWNER, owner);

            ArrayList<Map<String, Object>> members = new ArrayList<>();
            for (User m : testGroup.getMembers()) {
                Map<String, Object> member = new HashMap<>();
                member.put(USER_UID, m.getUid());
                member.put(USER_EMAIL, m.getEmail());
                members.add(member);
            }
            tg.put(TEST_GROUP_MEMBERS, members);

            testGroupsRef
                    .document(testGroup.get_id())
                    .update(tg)
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (onCompleteListener != null)
                                    onCompleteListener.onComplete(testGroup);
                                Timber.d("test group(" + testGroup + ") is added");
                            } else {
                                if (onCompleteListener != null)
                                    onCompleteListener.onException(task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * @param test               Test
     * @param onCompleteListener
     * @brief 수행평가를 추가합니다.
     */
    public void addTest(Test test, OnCompleteListener<Test> onCompleteListener) {
        if (test != null) {
            Map<String, Object> t = new HashMap();
            t.put(TEST_GROUP, test.getGroup());
            t.put(TEST_NAME, test.getName());
            t.put(TEST_SUBJECT, test.getSubject());
            t.put(TEST_DATE, test.getDateString());

            Map<String, Object> content = new HashMap<>();
            content.put(CONTENT_CONTENT, Blob.fromBytes(test.getContent().getContent()));
            content.put(CONTENT_FILE_NAME, test.getContent().getFileName());
            content.put(CONTENT_EXTENSION, test.getContent().getExtension());
            t.put(TEST_CONTENT, content);

            t.put(TEST_EXPECTED_TIME, test.getExpectedTime());
            t.put(TEST_PUB_DATE, DateTimeFormat.forPattern(PUB_DATE_FORMAT).print(DateTime.now()));

            testsRef
                    .add(t)
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                DocumentReference document = task.getResult();
                                test.set_id(document.getId());
                                if (onCompleteListener != null)
                                    onCompleteListener.onComplete(test);
                                Timber.d("test(" + test + ") is added");
                                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                                addLog(new Log(
                                        new User(currUser.getUid(), currUser.getEmail()),
                                        test.get_id(),
                                        false,
                                        DateTime.now()
                                ), new OnCompleteListener<Log>() {
                                    @Override
                                    public void onComplete(Log result) {
                                        Timber.d(result.toString());
                                    }

                                    @Override
                                    public void onException(Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                if (onCompleteListener != null)
                                    onCompleteListener.onException(task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * @param test               Test
     * @param onCompleteListener
     * @brief 수행평가를 변경합니다.
     */
    public void updateTest(Test test, OnCompleteListener<Test> onCompleteListener) {
        if (test != null) {
            Map<String, Object> t = new HashMap();
            t.put(TEST_GROUP, test.getGroup());
            t.put(TEST_NAME, test.getName());
            t.put(TEST_SUBJECT, test.getSubject());
            t.put(TEST_DATE, test.getDateString());

            Map<String, Object> content = new HashMap<>();
            content.put(CONTENT_CONTENT, Blob.fromBytes(test.getContent().getContent()));
            content.put(CONTENT_FILE_NAME, test.getContent().getFileName());
            content.put(CONTENT_EXTENSION, test.getContent().getExtension());
            t.put(TEST_CONTENT, content);

            t.put(TEST_EXPECTED_TIME, test.getExpectedTime());
            t.put(TEST_PUB_DATE, test.getPubDateStringForDB());
            testsRef
                    .document(test.get_id())
                    .update(t)
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (onCompleteListener != null)
                                    onCompleteListener.onComplete(test);
                                Timber.d("test (" + test + ") is updated");
                                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                                addLog(new Log(
                                        new User(currUser.getUid(), currUser.getEmail()),
                                        test.get_id(),
                                        true,
                                        DateTime.now()
                                ), new OnCompleteListener<Log>() {
                                    @Override
                                    public void onComplete(Log result) {
                                        Timber.d(result.toString());
                                    }

                                    @Override
                                    public void onException(Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                if (onCompleteListener != null)
                                    onCompleteListener.onException(task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * @brief 모든 수행평가 그룹을 반환합니다.
     */
    public void getTestGroup(OnCompleteListener<ArrayList<TestGroup>> onCompleteListener) {
        ArrayList<TestGroup> result = new ArrayList<>();
        Map<String, Object> cu = new HashMap<>();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        cu.put(USER_UID, currUser.getUid());
        cu.put(USER_EMAIL, currUser.getEmail());
        testGroupsRef
                .whereArrayContains(TEST_GROUP_MEMBERS, cu)
                .get()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> tg = document.getData();

                                Map<String, Object> o = (Map<String, Object>) tg.get(TEST_GROUP_OWNER);

                                ArrayList<Map<String, Object>> ms = (ArrayList<Map<String, Object>>) tg.get(TEST_GROUP_MEMBERS);
                                ArrayList<User> members = new ArrayList<>();
                                for (Map<String, Object> m : ms) {
                                    members.add(new User((String) m.get(USER_UID), (String) m.get(USER_EMAIL)));
                                }

                                result.add(
                                        new TestGroup(
                                                document.getId(),
                                                (String) tg.get(TEST_GROUP_NAME),
                                                (String) tg.get(TEST_GROUP_PUB_DATE),
                                                new User((String) o.get(USER_UID), (String) o.get(USER_EMAIL)),
                                                members
                                        ));
                            }
                            testGroupsRef
                                    .whereEqualTo(TEST_GROUP_OWNER, cu)
                                    .get()
                                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Map<String, Object> tg = document.getData();

                                                    Map<String, Object> o = (Map<String, Object>) tg.get(TEST_GROUP_OWNER);

                                                    ArrayList<Map<String, Object>> ms = (ArrayList<Map<String, Object>>) tg.get(TEST_GROUP_MEMBERS);
                                                    ArrayList<User> members = new ArrayList<>();
                                                    for (Map<String, Object> m : ms) {
                                                        members.add(new User((String) m.get(USER_UID), (String) m.get(USER_EMAIL)));
                                                    }

                                                    result.add(
                                                            new TestGroup(
                                                                    document.getId(),
                                                                    (String) tg.get(TEST_GROUP_NAME),
                                                                    (String) tg.get(TEST_GROUP_PUB_DATE),
                                                                    new User((String) o.get(USER_UID), (String) o.get(USER_EMAIL)),
                                                                    members
                                                            ));
                                                }
                                                onCompleteListener.onComplete(result);
                                            } else {
                                                onCompleteListener.onException(task.getException());
                                            }
                                        }
                                    });
                        } else {
                            onCompleteListener.onException(task.getException());
                        }
                    }
                });

    }

    /**
     * @param _id                String
     * @param onCompleteListener OnCompleteListener<TestGroup>
     * @brief 특정 수행평가 그룹을 반환합니다.
     */
    public void getTestGroup(String _id, OnCompleteListener<TestGroup> onCompleteListener) {
        testGroupsRef
                .document(_id)
                .get()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (task.isSuccessful()) {
                            Map<String, Object> tg = document.getData();
                            Map<String, Object> o = (Map<String, Object>) tg.get(TEST_GROUP_OWNER);

                            ArrayList<Map<String, Object>> ms = (ArrayList<Map<String, Object>>) tg.get(TEST_GROUP_MEMBERS);
                            ArrayList<User> members = new ArrayList<>();
                            for (Map<String, Object> m : ms) {
                                members.add(new User((String) m.get(USER_UID), (String) m.get(USER_EMAIL)));
                            }
                            if (onCompleteListener != null)
                                onCompleteListener.onComplete(new TestGroup(
                                        document.getId(),
                                        (String) tg.get(TEST_GROUP_NAME),
                                        (String) tg.get(TEST_GROUP_PUB_DATE),
                                        new User((String) o.get(USER_UID), (String) o.get(USER_EMAIL)),
                                        members
                                ));
                        } else {
                            if (onCompleteListener != null)
                                onCompleteListener.onException(task.getException());
                        }
                    }
                });
    }


    /**
     * @param group              int
     * @param onCompleteListener
     * @brief 해당 그룹의 모든 수행평가를 반환합니다.
     */
    public void getTestByGroup(String group, OnCompleteListener<ArrayList<Test>> onCompleteListener) {
        testsRef
                .whereEqualTo(TEST_GROUP, group)
                .get()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Test> result = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map t = document.getData();
                                Map c = (Map<String, Object>) t.get(TEST_CONTENT);
                                result.add(new Test(
                                        document.getId(),
                                        (String) t.get(TEST_GROUP),
                                        (String) t.get(TEST_NAME),
                                        (String) t.get(TEST_SUBJECT),
                                        (String) t.get(TEST_DATE),
                                        new Content((String) c.get(CONTENT_FILE_NAME), (String) c.get(CONTENT_EXTENSION), ((Blob) c.get(CONTENT_CONTENT)).toBytes()),
                                        (Long) t.get(TEST_EXPECTED_TIME),
                                        (String) t.get(TEST_PUB_DATE)
                                ));
                            }
                            onCompleteListener.onComplete(result);
                        } else {
                            onCompleteListener.onException(task.getException());
                        }
                    }
                });
    }

    /**
     * @param onCompleteListener
     * @brief 모든 수행평가를 반환합니다.
     */
    public void getTest(OnCompleteListener<ArrayList<Test>> onCompleteListener) {
        ArrayList<Test> result = new ArrayList<>();
        getTestGroup(new OnCompleteListener<ArrayList<TestGroup>>() {
            @Override
            public void onComplete(ArrayList<TestGroup> testGroups) {
                android.util.Log.d("SubjectFragment",testGroups.toString());
                if (testGroups.isEmpty()) {
                    onCompleteListener.onComplete(result);
                    return;
                }
                for (int i = 0; i < testGroups.size(); i++) {
                    int finalI = i;
                    getTestByGroup(testGroups.get(i).get_id(), new OnCompleteListener<ArrayList<Test>>() {
                        @Override
                        public void onComplete(ArrayList<Test> tests) {
                            android.util.Log.d("SubjectFragment"+finalI,tests.toString());
                            result.addAll(tests);
                            if (finalI == testGroups.size() - 1)
                                onCompleteListener.onComplete(result);
                        }

                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param onCompleteListener
     * @return ArrayList<Test>
     * @brief 모든 수행평가 과목을 반환합니다.
     */
    public void getSubejctOfTest(OnCompleteListener<ArrayList<String>> onCompleteListener) {
        ArrayList<String> subjects = new ArrayList<>();
        getTest(new OnCompleteListener<ArrayList<Test>>() {
            @Override
            public void onComplete(ArrayList<Test> result) {
                android.util.Log.d("SubjectFragment",result.toString());
                for (Test test : result) {
                    if (!subjects.contains(test.getSubject()))
                        subjects.add(test.getSubject());
                }
                if (onCompleteListener != null)
                    onCompleteListener.onComplete(subjects);
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                onCompleteListener.onException(e);
            }
        });
    }

    /**
     * @param subject            String
     * @param onCompleteListener
     * @return ArrayList<Test>
     * @brief 해당 과목의 모든 수행평가를 반환합니다.
     */
    public void getTestBySubject(String subject, OnCompleteListener<ArrayList<Test>> onCompleteListener) {
        ArrayList<Test> result = new ArrayList<>();
        getTest(new OnCompleteListener<ArrayList<Test>>() {
            @Override
            public void onComplete(ArrayList<Test> tests) {
                for (Test test : tests) {
                    if (test.getSubject().equals(subject))
                        result.add(test);
                }
                if (onCompleteListener != null)
                    onCompleteListener.onComplete(result);
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                if (onCompleteListener != null)
                    onCompleteListener.onException(e);
            }
        });
    }

    /**
     * @param testGroup          TestGroup
     * @param onCompleteListener
     * @brief 수행평가 그룹 삭제하기
     */
    public void removeTestGroup(@NotNull TestGroup testGroup, OnCompleteListener<TestGroup> onCompleteListener) {
        testGroupsRef
                .document(testGroup.get_id())
                .delete()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (onCompleteListener != null)
                                onCompleteListener.onComplete(testGroup);
                            testsRef.whereEqualTo(TEST_GROUP, testGroup.get_id()).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete();
                                        }
                                    } else {
                                        task.getException().printStackTrace();
                                    }
                                }
                            });
                        } else {
                            if (onCompleteListener != null)
                                onCompleteListener.onException(task.getException());
                        }
                    }
                });
    }

    /**
     * @param test               Test
     * @param onCompleteListener
     * @brief 수행평가 삭제하기
     */
    public void removeTest(@NotNull Test test, OnCompleteListener<Test> onCompleteListener) {
        android.util.Log.d("Remove", test.toString());
        testGroupsRef
                .document(test.get_id())
                .delete()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (onCompleteListener != null)
                                onCompleteListener.onComplete(test);
                        } else {
                            if (onCompleteListener != null)
                                onCompleteListener.onException(task.getException());
                        }
                    }
                });
    }

    public void addLog(Log log, OnCompleteListener<Log> onCompleteListener) {
        Map<String, Object> l = new HashMap<>();

        Map<String, Object> u = new HashMap<>();
        u.put(USER_UID, log.getUser().getUid());
        u.put(USER_EMAIL, log.getUser().getEmail());
        l.put(LOG_USER, u);
        l.put(LOG_TEST, log.getTest());
        l.put(LOG_DATE, log.getStringOfDate());
        l.put(LOG_IS_UPDATE, log.getUpdate());
        logsRef.add(l).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    log.set_id(task.getResult().getId());
                    if (onCompleteListener != null)
                        onCompleteListener.onComplete(log);
                } else {
                    if (onCompleteListener != null)
                        onCompleteListener.onException(task.getException());
                }
            }
        });
    }

    public void getLog(String test_id, OnCompleteListener<ArrayList<Log>> onCompleteListener) {
        logsRef.whereEqualTo(LOG_TEST, test_id).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Log> result = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map l = document.getData();
                        Map u = (Map<String, Object>) l.get(LOG_USER);
                        result.add(new Log(
                                document.getId(),
                                new User((String) u.get(USER_UID), (String) u.get(USER_EMAIL)),
                                (String) l.get(LOG_TEST),
                                (Boolean) l.get(LOG_IS_UPDATE),
                                (String) l.get(LOG_DATE)
                        ));
                    }
                    if (onCompleteListener != null)
                        onCompleteListener.onComplete(result);
                } else {
                    if (onCompleteListener != null)
                        onCompleteListener.onException(task.getException());
                }
            }
        });
    }
}
