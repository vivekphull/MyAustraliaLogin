package com.pavi_developing.myaustralialogin;



/**
 * Created by Hp-core.i5 on 02-09-2017.
 */


import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.ArrayList;

public class DatabaseManager {

    private static final String TAG = "DynamoDBManager";

    /*
     * Retrieves the table description and returns the table status as a string.
     */
    public static String getTestTableStatus() {

        try {
            AmazonDynamoDBClient ddb = PrepareReport.clientManager
                    .ddb();

            DescribeTableRequest request = new DescribeTableRequest()
                    .withTableName(Constants.TEST_TABLE_NAME);
            DescribeTableResult result = ddb.describeTable(request);

            String status = result.getTable().getTableStatus();
            return status == null ? "" : status;

        } catch (ResourceNotFoundException e) {
        } catch (AmazonServiceException ex) {
            PrepareReport.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return "";
    }

    public static void insertreport(String counsil,String description,String location,String userid) {
        AmazonDynamoDBClient ddb = PrepareReport.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            for (int i = 1; i <= 2; i++) {
                UserPreference userPreference = new UserPreference();
                userPreference.setReport_id("1");
                userPreference.setCounsil(counsil);
                userPreference.setDescription(description);
                userPreference.setLocation(location);
                userPreference.setUser_id(userid);
                mapper.save(userPreference);
            }
        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error inserting report");
            PrepareReport.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }


    @DynamoDBTable(tableName = Constants.TEST_TABLE_NAME)
    public static class UserPreference {
        private String report_id;
        private String counsil;
        private String description;
        private String image;
        private String location;
        private String user_id;

        @DynamoDBHashKey(attributeName = "report_id")
        public String getReport_id() {
            return report_id;
        }

        public void setReport_id(String report_id) {
            this.report_id = report_id;
        }

        @DynamoDBAttribute(attributeName = "counsil")
        public String getCounsil() {
            return counsil;
        }

        public void setCounsil(String counsil) {
            this.counsil = counsil;
        }

        @DynamoDBAttribute(attributeName = "description")
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @DynamoDBAttribute(attributeName = "image")
        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @DynamoDBAttribute(attributeName = "location")
        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @DynamoDBAttribute(attributeName = "user_id")
        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

    }
}
