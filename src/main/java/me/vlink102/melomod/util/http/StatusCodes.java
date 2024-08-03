package me.vlink102.melomod.util.http;

/**
 * HTTP Status Code constants.
 */
public enum StatusCodes {
    ACCEPTED(202),
    BAD_GATEWAY(502),
    BAD_METHOD(405),
    BAD_REQUEST(400),
    CLIENT_TIMEOUT(408),
    CONFLICT(409),
    CREATED(201),
    ENTITY_TOO_LARGE(413),
    FORBIDDEN(403),
    GATEWAY_TIMEOUT(504),
    GONE(410),
    INTERNAL_ERROR(500),
    LENGTH_REQUIRED(411),
    MOVED_PERM(301),
    MOVED_TEMP(302),
    MULT_CHOICE(300),
    NOT_ACCEPTABLE(406),
    NOT_AUTHORITATIVE(203),
    NOT_FOUND(404),
    NOT_IMPLEMENTED(501),
    NOT_MODIFIED(304),
    NO_CONTENT(204),
    OK(200),
    PARTIAL(206),
    PAYMENT_REQUIRED(402),
    PRECON_FAILED(412),
    PROXY_AUTH(407),
    REQ_TOO_LONG(414),
    RESET(205),
    SEE_OTHER(303),
    UNAUTHORIZED(401),
    UNAVAILABLE(503),
    UNSUPPORTED_TYPE(415),
    USE_PROXY(305),
    VERSION(505);

    private final int statusCode;

    StatusCodes(int statusCode) {
        this.statusCode = statusCode;
    }


    public static StatusCodes getStatusCode(int statusCode) {
        for (StatusCodes status : StatusCodes.values()) {
            if (status.statusCode == statusCode) {
                return status;
            }
        }
        return null;
    }

    public static String getFromCode(int code) {
        for (StatusCodes value : StatusCodes.values()) {
            if (value.getStatusCode() == code) {
                return value.name();
            }
        }
        return "Unknown";
    }

    public int getStatusCode() {
        return statusCode;
    }
}
