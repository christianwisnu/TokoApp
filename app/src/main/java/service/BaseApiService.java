package service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import utilities.JSONResponse;

import static com.firebase.client.utilities.HttpUtilities.HttpRequestType.GET;

/**
 * Created by christian on 14/02/18.
 */

public interface BaseApiService {

    @FormUrlEncoded
    @POST("edit_profile.php")
    Call<ResponseBody> editProfile(@Field("id") String id,
                                    @Field("telp") String telp,
                                    @Field("birthday") String birthday,
                                    @Field("alamat") String alamat,
                                    @Field("gender") String gender,
                                    @Field("foto") String foto,
                                    @Field("tglNow") String tanggalNow,
                                    @Field("device") String deviceName,
                                    @Field("nama") String nama,
                                    @Field("email") String email);

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginRequest(@Field("nama") String nama,
                                    @Field("email") String email,
                                    @Field("id") String id,
                                    @Field("tglLahir") String tanggal,
                                    @Field("foto") String foto,
                                    @Field("tglNow") String tanggalNow,
                                    @Field("device") String deviceName,
                                    @Field("loginWith") String loginWith,
                                    @Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("id") String id,
                                       @Field("tglLahir") String tanggal,
                                       @Field("foto") String foto,
                                       @Field("tglNow") String tanggalNow,
                                       @Field("device") String deviceName,
                                       @Field("loginWith") String loginWith,
                                       @Field("phone") String phone);

    @FormUrlEncoded
    @POST("registerMerchant.php")
    Call<ResponseBody> registerMerchant(@Field("IDToko") String idToko,
                                        @Field("namaToko") String namaToko,
                                        @Field("slogan") String slogan,
                                        @Field("deskripsi") String deskripsi,
                                        @Field("note") String note,
                                        @Field("provinsi") String provinsi,
                                        @Field("kabkota") String kabkota,
                                        @Field("kecamatan") String kecamatan,
                                        @Field("alamat") String alamatMap,
                                        @Field("latt") double latt,
                                        @Field("longt") double longt,
                                        @Field("kodepos") String kodepos,
                                        @Field("stayed") Integer stayed,//jualan keliling/menetap?
                                        @Field("emailToko") String emailToko,
                                        @Field("noWa") String noWa,
                                        @Field("noTlp1") String noTlp1,
                                        @Field("noTlp2") String noTlp2,
                                        @Field("noFax") String noFax,
                                        @Field("fb") String fb,
                                        @Field("twitter") String twitter,
                                        @Field("ig") String ig,
                                        @Field("openMon") String openMon,
                                        @Field("closeMon") String closeMon,
                                        @Field("statusMon") Integer statusMon,
                                        @Field("openTue") String openTue,
                                        @Field("closeTue") String closeTue,
                                        @Field("statusTue") Integer statusTue,
                                        @Field("openWed") String openWed,
                                        @Field("closeWed") String closeWed,
                                        @Field("statusWed") Integer statusWed,
                                        @Field("openThu") String openThu,
                                        @Field("closeThu") String closeThu,
                                        @Field("statusThu") Integer statusThu,
                                        @Field("openFri") String openFri,
                                        @Field("closeFri") String closeFri,
                                        @Field("statusFri") Integer statusFri,
                                        @Field("openSat") String openSat,
                                        @Field("closeSat") String closeSat,
                                        @Field("statusSat") Integer statusSat,
                                        @Field("openSun") String openSun,
                                        @Field("closeSun") String closeSun,
                                        @Field("statusSun") Integer statusSun,
                                        @Field("cdStatus") String cdStatus,
                                        @Field("user") String user01,
                                        @Field("dtUser") String dtUser01,
                                        @Field("statusTrans") String statTransaksi,
                                        @Field("pathFoto") String foto);

    @Multipart
    @POST("upload.php?apicall=upload")
    Call<ResponseBody> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
                                   @Part("desc") RequestBody desc);

    @Multipart
    @POST("uploadToko.php?apicall=upload")
    Call<ResponseBody> uploadImageToko(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
                                   @Part("desc") RequestBody desc,
                                       @Part("folder") RequestBody folder);

    @GET("listProvinsi.php")
    Call<JSONResponse> getProvinsi();

    @FormUrlEncoded
    @POST("listKabKota.php")
    Call<JSONResponse> getKabKota(@Field("kodeProv") String kodeProv);

    @FormUrlEncoded
    @POST("listKecamatan.php")
    Call<JSONResponse> getKecamatan(@Field("kodeKab") String kodeKab);
}