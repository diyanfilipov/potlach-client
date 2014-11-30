package com.android.potlach.cloud.client;

import java.util.Collection;
import java.util.Date;

import com.android.potlach.entity.Gift;
import com.android.potlach.entity.GiftObsceneStatus;
import com.android.potlach.entity.GiftStatus;
import com.android.potlach.entity.GiftTouchStatus;
import com.android.potlach.entity.GiftTouches;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

public interface PotlachSvcApi {
	public static final String TITLE_PARAMETER = "title";
	
	public static final String USER_PARAMETER = "user";

	public static final String DATE_PARAMETER = "date";

    public static final String PARENT_PARAMETER = "parent";

    public static final String OBSCENE_PARAMETER = "o";

    public static final String START_PARAMETER = "s";

    public static final String END_PARAMETER = "e";

    public static final String ID_PARAMETER = "id";

    public static final String DATA_PARAMETER = "data";

	public static final String TOKEN_PATH = "/oauth/token";

	public static final String GIFT_PATH = "/gift";

	public static final String REGISTER_PATH = "/register";

    public static final String GIFT_IMAGE_PATH = GIFT_PATH + "{id}/image";

	public static final String GIFT_CHAIN_PATH = GIFT_PATH + "/giftChain";
	
	public static final String GIFT_CHAIN_BY_GIFT_PATH = GIFT_PATH + "/giftChainByGift";

    public static final String POTLACH_SIGN_IN_PATH = "/signin";

    public static final String ALL_GIFT_CHAINS_PATH = GIFT_PATH + "/chains";

	public static final String GIFT_TITLE_SEARCH_PATH = GIFT_PATH + "/search/findByName";
	
	public static final String GIFT_USER_SEARCH_PATH = GIFT_PATH + "/search/findByUser";
	
	public static final String GIFT_MOST_RECENT_SEARCH_PATH = GIFT_PATH + "/search/mostRecent";
	
	@POST(GIFT_PATH + "/{giftChain}")
	public Gift postGift(@Body Gift gift, @Path("giftChain") String giftChain);

    @Multipart
    @POST(GIFT_IMAGE_PATH)
    public GiftStatus addGiftImage(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile imageData);

    @Streaming
    @GET(GIFT_IMAGE_PATH)
    public Response getGiftImage(@Path(ID_PARAMETER) long id);

    @GET(GIFT_CHAIN_PATH + "/{giftChain}")
    public Collection<Gift> getGiftsByChain(
            @Path("giftChain") String giftChain,
            @Query(START_PARAMETER) int start,
            @Query(END_PARAMETER) int end,
            @Query(OBSCENE_PARAMETER) boolean obscene);
	
	@GET(GIFT_PATH + "/{id}")
	public Gift getGiftById(@Path("id") long id);
	
	@GET(GIFT_PATH)
	public Collection<Gift> getAllGifts();
	
	@GET(GIFT_CHAIN_BY_GIFT_PATH + "/{id}")
	public Gift getGiftChainByGift(@Path("id") long id);

    @GET(ALL_GIFT_CHAINS_PATH)
    public Collection<Gift> getAllGiftChains();

    @GET(GIFT_USER_SEARCH_PATH)
    public Collection<Gift> findByUploader(@Query(USER_PARAMETER) String user,
                                           @Query(START_PARAMETER) int start,
                                           @Query(END_PARAMETER) int end,
                                           @Query(OBSCENE_PARAMETER) boolean showObscene);
	
	@GET(GIFT_TITLE_SEARCH_PATH)
	public Collection<Gift> findByTitle(
            @Query(TITLE_PARAMETER) String title,
            @Query(START_PARAMETER) int start,
            @Query(END_PARAMETER) int end,
            @Query(OBSCENE_PARAMETER) boolean showObscene);

    @GET(GIFT_MOST_RECENT_SEARCH_PATH)
    public Collection<Gift> findMostRecentGifts(@Query(DATE_PARAMETER) long date, @Query(PARENT_PARAMETER) long parent);
	
	@POST(GIFT_PATH + "/{id}/touch")
	public GiftTouchStatus touch(@Path("id") long id);

    @GET(GIFT_PATH + "/{id}/touches")
    public int getTouchesCount(@Path("id") long id);

    @GET(GIFT_PATH + "/{id}/{username}/touches")
    public GiftTouches getTouchesByUser(@Path("id") long id, @Path("username") String username);

    @POST(GIFT_PATH + "/{id}/obscene")
    public GiftObsceneStatus obscene(@Path("id") long id);

    @GET(GIFT_PATH + "/{id}/obscene/status")
    public GiftObsceneStatus obsceneStatus(@Path("id") long id);
	
	@GET(GIFT_PATH + "/top")
	public Collection<Object[]> getTopGiftGivers(
            @Query(START_PARAMETER) int start,
            @Query(END_PARAMETER) int end
    );
	
	@POST(REGISTER_PATH)
	public boolean register(@Query("username") String username, @Query("password") String password);

    @POST(POTLACH_SIGN_IN_PATH)
    public boolean signIn();
}
