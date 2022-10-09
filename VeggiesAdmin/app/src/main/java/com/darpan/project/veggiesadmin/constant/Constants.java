package com.darpan.project.veggiesadmin.constant;

public class Constants {


    /*preference constants*/
    public static final String NOTI_COUNT="notificationCount";
    public static final String PREF_NAME="sharedPreferencesName";
    public static final String TOTAL_PENDING="totalPendingOrder";
    public static final String TOTAL_DELIVERED="totalOrderDelivered";
    public static final String TOTAL_CANCELED="totalOrderCancelled";
    public static final String TOTAL_SALES="totalSales";
    public static final String TOTAL_FEEDBACK="totalFeedBack";
    public static final String TOTAL_CUSTOMER="totalCustomerInDataBase";
    public static final int TOTAL_DEFAULT=0;
    public static final String TOTAL_TODAY="totalOrderToday";
    public static final String TOTAL_REJECTED="totalRejectedOrder";


    /*permission request*/
    public static final int PICK_IMAGE_REQUEST = 1;

    /*Notification id*/
    public static final String ConfirmNotification = "ConfirmNotification_For_the admin";

    public static final String Client_NOTI = "order_request_from_client_notification_id";


    /*firestore collection*/

    public static final String FIREBASE_USER_ID = "userID";

    public static final String USER_COLLECTION = "veggiesApp/Veggies/user";
    public static final String BANNER_COLLECTION = "veggiesApp/Veggies/banner";
    public static final String CATEGORY_COLLECTION = "veggiesApp/Veggies/category";
    public static final int PAGE_COUNT = 10;
    public static final int LIMIT = 10;
    public static final String PRODUCT_LIST_COLLECTION = "veggiesApp/Veggies/productList";
    public static final String AREA_COLLEC = "veggiesApp/Veggies/area";

    public static final String COMPLETED_ORDER_COLLEC="veggiesApp/Veggies/completedOrder";

    public static final String FEEDBACK_COLLEC="veggiesApp/Veggies/feedback";
    public static final String GLOBAL_BLOCK_COLLEC="veggiesApp/Veggies/globalBlock";


    /*delivery collection*/
    public static final String DELIVERY_TIME_COLLEC = "veggiesApp/Veggies/deliveryTime";
    public static final String GLOBAL_TIME_SLOT_KEY = "globalDeliveryTime";//key
    public static final String DELIVERY_TIME__DOC_NAME = "deliveryTimeSlot";//document name



    public static final String CUSTOMER_KEY="customerId";



    /*FireStore storage Variable*/

    public static final String CATEGORY_IMAGE = "Veggies/categoryImages";
    public static final String CATEGORY_REF = "veggiesApp/Veggies/category";
    public static final String BANNER_IMAGE_STORAGE_REF="Veggies/bannerImages";
    public static final String PRODUCT_IMAGES_STORAGE_REF="Veggies/productImages";
    public static final String SUBCATEGORY_IMAGES_STORAGE_REF="Veggies/subcategoryImages";
    public static final String USER_PROFILE_STORAGE_REF="Veggies/userProfileImages";

    /*category collection doc keys*/
    public static final String categoryName = "categoryName";
    public static final String categoryImage = "categoryImage";
    public static final String productId="productId";

    /*product collection document keys*/
    public static final String productImage="productImage";



    /*Token*/
    /*collection name=adminToken
     * document name=token
     * field name=tokenKey of type string*/


    /*user token inside  the user collection*/
    /*collection name=userToken
     * document name=token
     * field name=tokenKey of type string*/

    public static final String USER_TOKEN_COLLEC = "userToken";
    public static final String ADMIN_TOKEN_COLLEC = "veggiesApp/Veggies/adminToken";
    public static final String TOKEN_DOC = "token";
    public static final String TOKEN_KEY = "tokenKey";

    /*Order Collection Constant*/
    public static final String ORDER_PLACED_COLLEC = "veggiesApp/Veggies/orderPlaced";//admin orders
    public static final String ORDERED_ITEM_COLLEC = "orderedProductDetails";
    public static final String USER_ORDER_COLLEC = "userOrder";
    public static final String ORDER_STATUS_COLLEC = "orderStatus";


    /*user collection for sending fcm*/
    public static final String USER_COLLEC = "veggiesApp/Veggies/user";
    public static final String USER_ORDER_ID = "orderId";




    /*area Block Collection*/
    public static final String AREA_BLOCK_COLLEC="veggiesApp/Veggies/areaBlocks";
    public static final String AREA_KEY_BLOCK_NAME="blockName";
    public static final String AREA_KEY_BLOCK_NUMBER="blockNumber";

    /*order Status Constants*/
    public static final String PENDING = "Pending";
    public static final String ACCEPTED = "Accepted";
    public static final String CANCELLED="Cancelled";//user cancelled the order
    public static final String REJECTED="Rejected";//admin rejected the order
    public static final String DELIVERED="Delivered";
    public static final String TODAY="Today's Order";//admin filter option

    /*product list filter option*/
    public static final String isPublished="isPublished";
    public static final String OPTION_QTY="optionQty";
    public static final String CATEGORY="productCategory";
    public static final String UNIQUE_PID="UniquePid";
    public static final String VISIBLE="Visible To User";
    public static final String NOT_VISIBLE="InVisible To User";
    public static final String NO_ID="Item with No Id";
    public static final String NO_ID_VALUE="Create code";
    public static final String ZERO_PRICE="Item_with 0 Price";
    public static final String ZERO_DISC="Item With 0 Discount";

    public static final String O_F_S="Out Of Stock";




}
