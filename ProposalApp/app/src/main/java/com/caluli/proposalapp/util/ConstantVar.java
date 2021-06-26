package com.caluli.proposalapp.util;

public class ConstantVar {

    //fcm
    public static final String FCM_SERVER_KEY = "AAAAkTWSrvA:APA91bEkdTpblrGxSE-rE2c6oQMq9TjWKYkTelaQTmWsKTIGbRlAoZv9WAAPskbuOUE9cT90qZtV50NoAxCmjp0kZAaY53Y6XX66NowIia-hAAE4YTWnyCN_hew884TxurnNmg7rBeUf";

    //list of tables
    public static final String TABLE_USER = "user";
    public static final String TABLE_GROUP = "group";
    public static final String TABLE_PROPOSAL = "proposal";



    //table user
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_MATRIC = "user_matric_no";
    public static final String COLUMN_USER_CLASS = "user_class";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_PHONE = "user_phone";
    public static final String COLUMN_USER_IMAGE = "user_image";
    public static final String COLUMN_USER_CREATED_ON = "user_created_on";
    public static final String COLUMN_USER_IS_ADMIN = "user_is_admin";

    //table group
    public static final String COLUMN_GROUP_REF = "group_ref";
    public static final String COLUMN_GROUP_USERS = "group_users";
    public static final String COLUMN_GROUP_REGISTERED_ON = "group_registered_on";

    //table proposal
    public static final String COLUMN_PROPOSAL_GROUP = "proposal_group";
    public static final String COLUMN_PROPOSAL_TITLE = "proposal_title";
    public static final String COLUMN_PROPOSAL_OBJECTIVE = "proposal_objective";
    public static final String COLUMN_PROPOSAL_PROBLEM_STATEMENT = "proposal_problem_statement";
    public static final String COLUMN_PROPOSAL_ORGANIZATION = "proposal_organization";
    public static final String COLUMN_PROPOSAL_ORGANIZATION_ADDRESS = "proposal_organization_address";
    public static final String COLUMN_PROPOSAL_SUPERVISOR = "proposal_supervisor";
    public static final String COLUMN_PROPOSAL_COMMENTS = "proposal_comments";
    public static final String COLUMN_PROPOSAL_STATUS = "proposal_status";

    //proposal status
    public static final String PROPOSAL_STATUS_PENDING = "Pending";
    public static final String PROPOSAL_STATUS_APPROVED = "Approved";
    public static final String PROPOSAL_STATUS_NOT_APPROVED = "Not Approved";
    public static final String PROPOSAL_STATUS_ARCHIVED = "Archived";

    //arrays
    public static final String[] ARRAY_CLASS = {"DDT5A", "DDT5B", "DDT5C", "DDT5D"};

    //tiny db
    public static final String TNB_IS_ADMIN = "tnb_is_admin";
    public static final String TNB_USER_MATRIC = "tnb_user_matric";
    public static final String TNB_GROUP_ID = "tnb_group_id";
    public static final String TNB_PROPOSAL_ID = "tnb_proposal_id";


}