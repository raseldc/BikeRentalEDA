package com.eventsourcing.bankAccount.events;

import com.eventsourcing.bankAccount.domain.BankAccountAggregate;
import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.es.BaseEvent;
import lombok.Builder;
import lombok.Data;

@Data
public class BikeRentEvent extends BaseEvent {
    public static final String Bike_Rent_CREATED_V1 = "Bike_Rent_CREATED_V1";
    public static final String Bike_Rent_SagaStart = "Bike_Rent_SagaStart";
    public static final String Bike_Rent_SagaUpdateStatus1 = "Bike_Rent_SagaUpdateStatus1";
    public static  final String Bike_Rent_SagaUpdateStatusReadUpdate2="Bike_Rent_SagaUpdateStatusReadUpdate2";
    public static  final String Bike_Rent_SagaPaymentInitiate="Bike_Rent_SagaPaymentInitiate";
    public static  final String Bike_Rent_PaymentComplete="Bike_Rent_PaymentComplete";
    public static  final String Bike_Rent_PaymentCompleteFeedToSaga="Bike_Rent_PaymentCompleteFeedToSaga";

    public static final String AGGREGATE_TYPE = BikeAggregate.AGGREGATE_TYPE;

    @Builder
    public BikeRentEvent(String aggregateId, String bikeId, String bikeType, String location,String startDate,String endDate) {
        super(aggregateId);
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    private String bikeId;
    private String bikeType;
    private String location;
    private String startDate;
    private String endDate;
    private String status;
}