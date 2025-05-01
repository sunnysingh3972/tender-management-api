package com.fresco.tenderManagement.repository;

import com.fresco.tenderManagement.model.BiddingModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface BiddingRepository extends JpaRepository<BiddingModel,Integer> {

    List<BiddingModel> findByBidAmountGreaterThan(double bidAmount);

    //Add the required annotations to make the BiddingRepository
}
