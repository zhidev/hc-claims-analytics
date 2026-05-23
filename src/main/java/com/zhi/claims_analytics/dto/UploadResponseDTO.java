package com.zhi.claims_analytics.dto;

public class UploadResponseDTO {
    private boolean success;
    private int rowsInserted;
    private String message;

    public UploadResponseDTO (boolean success, int rowsInserted, String message){
        this.success = success;
        this.rowsInserted = rowsInserted;
        this.message = message;
    }

    public boolean isSuccess(){
        return success;
    }

    public int getRowsInserted(){
        return rowsInserted;
    }

    public String getMessage(){
        return message;
    }
}
