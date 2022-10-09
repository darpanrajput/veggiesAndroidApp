package com.darpan.project.vegies.constant;

public class Constants {

    public static final String IS_CORRECT_TIME="is_current_time_is_correct_for_delivery";
    public static final String PRIVACY_URL="https://androidgoogleplaystoreprivacypolicy.blogspot.com/2020/07/sai-dhuni-incproivacy-policy.html";
    public static final String TERMS="https://androidgoogleplaystoreprivacypolicy.blogspot.com/2020/07/sai-dhuni-inc-terms-and-condition.html";
    public static final String O_F_S="Out Of Stock";

    /*Notification id*/

    public static final String ORDER_NOTI = "order_successful_notification_id";
    public static final String CHANNEL_1_ID = "channelId1";
    public static final String ADMIN_NOTI = "order_Accepted_notification_id";
    /*preferences name*/

    public static final String FIRST_TIME = "com.darpan.project.vegies.constant.First_Time_open";
    public static final String PREF_NAME = "SharedPreferences";
    public static final String NOTI_COUNT = "notification_count_send_by_admin";

    /*FIRESTORE KEYS */
    public static final String FIREBASE_USER_ID = "userId";
    public static final String USER_COLLECTION = "veggiesApp/Veggies/user";
    public static final String BANNER_COLLECTION = "veggiesApp/Veggies/banner";
    public static final String CATEGORY_COLLECTION = "veggiesApp/Veggies/category";
    public static final int PAGE_COUNT = 9;
    public static final int LIMIT = 10;
    public static final String PRODUCT_LIST_COLLECTION = "veggiesApp/Veggies/productList";
    public static final String AREA_COLLEC = "veggiesApp/Veggies/area";

    public static final String PRODUCT_CLASS = "product_class";
    public static final String ORDER_CLASS = "orderPlacedClas";
    public static final String ITEM_PRICE_LIST = "eachItemActualPriceList";
    public static final String ITEM_QTY_LIST = "eachItemDemandedQTYList";


    /*banner collection keys*/
    public static final String KEY_BANNER_IMAGE = "bannerImage";
    public static final String KEY_BANNER_NAME = "bannerName";

    public static final String HASH_KEY = "hash";

    /*category collection doc keys*/
    public static final String categoryName = "categoryName";
    public static final String categoryImage = "categoryImage";

    /*area collection doc keys*/
    public static final String areaName = "areaName";
    public static final String isPublished = "publishStatus";
    public static final String areaDeliverCharge = "deliveryCharge";

    /*user collection doc keys*/
    public static final String FIRESTORE_KEY_USER_NAME = "name";
    public static final String FIRESTORE_KEY_EMAIL = "email";
    public static final String FIRESTORE_KEY_PHOTOURL = "photoUrl";
    public static final String FIRESTORE_KEY_PHONE = "mobile";


    public static final String u_blockName = "blockName";
    public static final String u_blockNo = "blockNo";
    public static final String u_email = "email";
    public static final String u_fullAddress = "fullAddress";
    public static final String u_landmark = "landmark";
    public static final String u_mobile = "mobile";
    public static final String u_username = "name";
    public static final String u_photoUrl = "photoUrl";
    public static final String u_pin = "pin";
    public static final String u_status = "status";
    public static final String u_userId = "userId";

    public static final String USER_IMAGE_STORAGE_PATH_NAME = "userProfileImages";

    /*area blocks collection*/
    public static final String areaBlock_collection_name = "veggiesApp/Veggies/areaBlocks";
    public static final String BLOCK_NUMBER = "blockNumber";
    public static final String BLOCK_NAME = "blockName";


    /*order constant*/

    public static final String ORDER_DATE = "orderDate";
    public static final String ORDER_TIME = "orderTime";
    public static final String PAYMENT_MODE = "paymentMode";
    public static final String ORDER_AMOUNT = "orderAmount";


    /*product list constant*/
    public static final String PRODUCT_STOCK = "stockQuantity";
    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_QUANTITY = "productQuantity";
    public static final String PRODUCT_UNIT = "productUnit";
    public static final String IS_PUBLISHED="isPublished";

    public static final String UNIQUE_PID="UniquePid";
    public static final String OPTION_QTY="optionQty";

    /*local variable for user detail to be saved and checked before the order placed */
    public static final String DEFAULT = "DEFAULT";

    public static final String SP_AREA = "userArea";


    /*Area collection constant*/

    public static final String AREA_NAME = "areaName";
    public static final String DELIVERY_CHARGE = "deliveryCharge";


    /*delivery collection*/
    public static final String DELIVERY_TIME_COLLEC = "veggiesApp/Veggies/deliveryTime";
    public static final String GLOBAL_TIME_SLOT_KEY = "globalDeliveryTime";//key
    public static final String DELIVERY_TIME__DOC_NAME = "deliveryTimeSlot";//document name

    /*global block delivery*/
    public static final String GLOBAL_BLOCK_REF = "veggiesApp/Veggies/globalBlock";
    public static final String blockAllDeliveries_DOC_REF = "blockAllDeliveries";
    public static final String IS_BLOcked = "isBlocked";

    /*Order Collection Constant*/
    public static final String ORDER_PLACED_COLLEC = "veggiesApp/Veggies/orderPlaced";//admin orders
    public static final String ORDERED_ITEM_COLLEC = "orderedProductDetails";
    public static final String USER_ORDER_COLLEC = "userOrder";
    public static final String ORDER_STATUS_KEY = "orderStatus";
    public static final String ACCEPTED = "Accepted";
    public static final String CANCELLED = "Cancelled";
    public static final String DELIVERED = "Delivered";
    public static final String PENDING = "Pending";


    /*Token*/
    /*collection name=veggiesApp/Veggies/adminToken
     * document name=token
     * field name=tokenKey of type string*/


    /*user token inside  the user collection*/
    /*collection name=userToken
     * document name=token
     * field name=tokenKey of type string*/

    public static final String USER_TOKEN_COLLEC = "userToken";
    public static final String TOKEN_DOC = "token";
    public static final String TOKEN_KEY = "tokenKey";


    /*Feedback collection*/
    public static final String feedback_coll = "veggiesApp/Veggies/feedback";

    public static final String INTENT_SOURCE="intentSource";
    public static final String SINGLE_INTENT="intent_from_the_single_order_summary";
    public static final String MULTI_INTENT="intent_from_the_multi_order_summary";


    public static final String PRODUCT_ID="productId";

}
