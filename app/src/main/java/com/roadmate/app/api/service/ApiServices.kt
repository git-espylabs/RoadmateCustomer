package com.roadmate.app.api.service

import com.roadmate.app.api.response.*
import com.roadmate.shop.api.response.ProductOfferMaster
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    @POST("cuslogin")
    suspend fun customerLogin(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("cusreg")
    suspend fun customerSignUp(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("otpcheck")
    suspend fun verifyOTP(@Body jsonRequest : RequestBody): Response<OtpMaster>

    @GET("banner")
    suspend fun getAppBanners(): Response<AppBannerMaster>

    @GET("vetype")
    suspend fun getVehicleTypes(): Response<VehicleTypeMaster>

    @POST("brand")
    suspend fun getVehicleBrands(@Body jsonRequest : RequestBody): Response<VehicleBrandMaster>

    @POST("model")
    suspend fun getVehicleModels(@Body jsonRequest : RequestBody): Response<VehicleModelMaster>

    @GET("fuel_type")
    suspend fun getVehicleFuelTypes(): Response<VehicleFuelTypeMaster>

    @POST("vehmfuel")
    suspend fun getVehicleFuelTypeByModel(@Body jsonRequest : RequestBody): Response<VehicleFuelTypeMaster>

    @POST("user_vehicles")
    suspend fun insertCustomerNewVehicle(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("walletbalance")
    suspend fun getWalletDetails(@Body jsonRequest : RequestBody): Response<WalletMaster>

    @POST("post_querys")
    suspend fun insertCustomerQuery(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("vehicleuserlist")
    suspend fun geMyVehicles(@Body jsonRequest : RequestBody): Response<CustomerVehicleMaster>

    @POST("package")
    suspend fun getPackages(@Body jsonRequest : RequestBody): Response<PackageMaster>

    @POST("packagedel")
    suspend fun getPackageDetails(@Body jsonRequest : RequestBody): Response<PackageMaster>

    @POST("packshop")
    suspend fun getPackageShops(@Body jsonRequest : RequestBody): Response<PackageShopsMaster>

    @POST("packagefeaturelist")
    suspend fun getPackageFeatures(@Body jsonRequest : RequestBody): Response<PackageFeaturesMaster>

    @POST("packagebook")
    suspend fun bookPackage(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("rated_shops")
    suspend fun getRatedMechanicShops(@Body jsonRequest : RequestBody): Response<RatedShopsMaster>

    @GET("shop_categories")
    suspend fun getMoreServices(): Response<MoreServicesMaster>

    @POST("shops")
    suspend fun getAllShops(@Body jsonRequest : RequestBody): Response<NearShopsMaster>

    @POST("shopslist")
    suspend fun getShopDetails(@Body jsonRequest : RequestBody): Response<ShopDataMaster>

    @POST("shop_serviceslist")
    suspend fun getShopServices(@Body jsonRequest : RequestBody): Response<MoreServicesMaster>

    @POST("reviewlist")
    suspend fun getShopReviews(@Body jsonRequest : RequestBody): Response<ReviewMaster>

    @POST("review")
    suspend fun postShopReviews(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("mystorelist")
    suspend fun getStoreProducts(@Body jsonRequest : RequestBody): Response<MyStoreProductMaster>

    @POST("mystorelistuser")
    suspend fun getMyProducts(@Body jsonRequest : RequestBody): Response<MyStoreProductMaster>

    @POST("singlestorelist")
    suspend fun getProductDetails(@Body jsonRequest : RequestBody): Response<ProductDetailsMaster>

    @POST("mechnearestshop")
    suspend fun getNearestMechanicsShops(@Body jsonRequest : RequestBody): Response<NearShopsMaster>

    @POST("mechnearestshopall")
    suspend fun getAllMechanicsShops(@Body jsonRequest : RequestBody): Response<NearShopsMaster>

    @Multipart
    @POST("storelistinsert")
    suspend fun insertProductToStore(@Part userid: MultipartBody.Part,
                                     @Part pro_name: MultipartBody.Part,
                                     @Part price: MultipartBody.Part,
                                     @Part category: MultipartBody.Part,
                                     @Part description: MultipartBody.Part,
                                     @Part usertype: MultipartBody.Part,
                                     @Part image1: MultipartBody.Part,
                                     @Part image2: MultipartBody.Part,
                                     @Part image3: MultipartBody.Part): Response<RoadmateApiResponse>

    @POST("offerlistveh")
    suspend fun getCustomerOffers(@Body jsonRequest : RequestBody): Response<CustomerOfferMaster>

    @POST("offerdetails")
    suspend fun getCustomerOfferDetails(@Body jsonRequest : RequestBody): Response<CustomerOfferMaster>

    @POST("catshoplist")
    suspend fun getShopBookingListOnSelectedCat(@Body jsonRequest : RequestBody): Response<ShopBookingListMaster>

    @POST("shoplist")
    suspend fun getShopBookingDetails(@Body jsonRequest : RequestBody): Response<ShopBookingDetailsMaster>

    @POST("availabletime")
    suspend fun getAvailableShopTimings(@Body jsonRequest : RequestBody): Response<AvailableTimeSlotsMaster>

    @POST("sob_time")
    suspend fun insertTimeSlot(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("bookedhistory")
    suspend fun getBookedHistory(@Body jsonRequest : RequestBody): Response<BookedHistoryMaster>

    @POST("offerdetails")
    suspend fun getOfferDetails(@Body jsonRequest : RequestBody): Response<OfferDetailsMaster>


    @GET("quesanswer")
    suspend fun getAllFaqs(): Response<FaqMaster>

    @POST("queanswer")
    suspend fun getAllFaqAns(@Body jsonRequest : RequestBody): Response<FaqAnswersMaster>

    @POST("storequeryinsert")
    suspend fun askAQuestion(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("storanswer")
    suspend fun answerAQuestion(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("storebanner")
    suspend fun getStoreBanners(@Body jsonRequest : RequestBody): Response<AppBannerMaster>

    @POST("custranhistory")
    suspend fun getWalletTransactionHistory(@Body jsonRequest : RequestBody): Response<TransactionHistoryMaster>

    @POST("paymentuncompletedlist")
    suspend fun getPaymentList(@Body jsonRequest : RequestBody): Response<PaymentItemMaster>

    @POST("updatepaystatuscustomer")
    suspend fun insertPaymentToServer(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("gcmupdate")
    suspend fun registerForFcm(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("paymentuncompletedcount")
    suspend fun getUserPendingPaymentsCount(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("searchshop")
    suspend fun shopSearch(@Body jsonRequest : RequestBody): Response<SearchMater>

    @POST("uservehicledelete")
    suspend fun deleteVehicle(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("userlistedit")
    suspend fun userEditData(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("productofferdetails")
    suspend fun getProductOfferDetails(@Body jsonRequest : RequestBody): Response<ProductOfferMaster>

    @POST("shopnotificationlist")
    suspend fun getUserNotifications(@Body jsonRequest : RequestBody): Response<NotificationMaster>

    @POST("updatesalestatus")
    suspend fun setProductSoldout(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("app_version")
    suspend fun getAppVersionFromServer(@Body jsonRequest : RequestBody): Response<AppVersionMaster>
}