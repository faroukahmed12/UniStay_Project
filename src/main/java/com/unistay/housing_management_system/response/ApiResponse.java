package com.unistay.housing_management_system.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T>
{
    private boolean checkSuccess;
    private String message;
    private T data;

    // Constructors
    public ApiResponse(boolean checkSuccess, String message) {
        this();
        this.checkSuccess = checkSuccess;
        this.message = message;
    }
    public ApiResponse(boolean checkSuccess, T data) {
        this();
        this.checkSuccess = checkSuccess;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

}
