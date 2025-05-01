package com.fresco.tenderManagement.service;

import com.fresco.tenderManagement.model.BiddingModel;
import com.fresco.tenderManagement.model.UserModel;
import com.fresco.tenderManagement.repository.BiddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class BiddingService {

    @Autowired
    private BiddingRepository biddingRepository;

    @Autowired
    private UserService userService;

    // Add a new bidding
    public ResponseEntity<Object> postBidding(BiddingModel biddingModel) {
        try {
            String currentUsername = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            UserModel user = userService.getUserByEmail(currentUsername);
            String role = user.getRole().getRolename();
            if (!"BIDDER".equalsIgnoreCase(role)) {
                return new ResponseEntity<>("You don't have permission to add a bidding", HttpStatus.FORBIDDEN);
            }
            int bidderId = user.getId();
            biddingModel.setBidderId(bidderId);
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            biddingModel.setDateOfBidding(df.format(new java.util.Date()));

            BiddingModel saved = biddingRepository.save(biddingModel);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        }catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    // View a bidding by bidAmount
    public ResponseEntity<Object> getBidding(double bidAmount) {
        try {
            String currentUsername = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            UserModel user = userService.getUserByEmail(currentUsername);

            String role = user.getRole().getRolename();
            if (!"BIDDER".equalsIgnoreCase(role) && !"APPROVER".equalsIgnoreCase(role)) {
                return new ResponseEntity<>("You don't have permission to view bidding list", HttpStatus.FORBIDDEN);
            }

            List<BiddingModel> result = biddingRepository.findByBidAmountGreaterThan(bidAmount);
            if (result.isEmpty()) {
                return new ResponseEntity<>("no data available", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    // Update the status of a bidding
    public ResponseEntity<Object> updateBidding(int id, BiddingModel model) {
        try {
            String currentUsername = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            UserModel user = userService.getUserByEmail(currentUsername);

            String role = user.getRole().getRolename();
            if (!"APPROVER".equalsIgnoreCase(role)) {
                return new ResponseEntity<>("You don't have permission to update bidding", HttpStatus.FORBIDDEN);
            }

            Optional<BiddingModel> optional = biddingRepository.findById(id);
            if (optional.isPresent()) {
                BiddingModel existing = optional.get();
                existing.setStatus(model.getStatus());
                return new ResponseEntity<>(biddingRepository.save(existing), HttpStatus.OK);
            }

            return new ResponseEntity<>("Bidding not found", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a bidding (only if the user is a bidder or approver and the bidding is theirs or they have permission)
    public ResponseEntity<Object> deleteBidding(int id) {
        try {
            String currentUsername = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            UserModel user = userService.getUserByEmail(currentUsername);

            String role = user.getRole().getRolename();
            Optional<BiddingModel> optional = biddingRepository.findById(id);

            if (optional.isEmpty()) {
                return new ResponseEntity<>("Bidding not found", HttpStatus.BAD_REQUEST);
            }

            BiddingModel bidding = optional.get();

            boolean isApprover = "APPROVER".equalsIgnoreCase(role);
            boolean isBidderAndOwner = "BIDDER".equalsIgnoreCase(role) && user.getId() == bidding.getBidderId();

            if (isApprover || isBidderAndOwner) {
                biddingRepository.deleteById(id);
                return new ResponseEntity<>("Bidding deleted successfully", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("You don't have permission to delete this bidding", HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }
}
