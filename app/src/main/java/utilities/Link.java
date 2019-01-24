package utilities;

import service.BaseApiService;

/**
 * Created by christian on 14/02/18.
 */

public class Link {

    public static final String BASE_URL_API = "http://softchrist.com/tokoapp/php/";
    public static final String BASE_URL_IMAGE = "http://softchrist.com/tokoapp/image/";
    public static final String BASE_URL_IMAGE_MERCHANT = "http://softchrist.com/tokoapp/imageMerchant/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

    public static BaseApiService getImageService(){
        return RetrofitClient.getClient(BASE_URL_IMAGE).create(BaseApiService.class);
    }

    public static BaseApiService getImageMerchantService(String folder){
        return RetrofitClient.getClient(BASE_URL_IMAGE_MERCHANT+folder+"/").create(BaseApiService.class);
    }
}
