package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CreditCardController {

  @Autowired private CreditCardRepository creditCardRepository;

  @Autowired private UserRepository userRepository;

  /**
   * Adds a credit card to a user based on input data.
   *
   * @param payload The credit card details to be stored.
   * @return The saved credit card's ID.
   */
  @PostMapping("/credit-card")
  public ResponseEntity<Integer> addCreditCardToUser(
      @RequestBody AddCreditCardToUserPayload payload) {
    Optional<User> optionalUser = userRepository.findById(payload.getUserId());
    if (!optionalUser.isPresent()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    CreditCard creditCard = new CreditCard();
    creditCard.setIssuanceBank(payload.getIssuanceBank());
    creditCard.setNumber(payload.getNumber());
    creditCard.setUser(optionalUser.get());

    creditCardRepository.save(creditCard);

    return new ResponseEntity<>(creditCard.getId(), HttpStatus.OK);
  }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<CreditCard> creditCards = creditCardRepository.findByUserId(userId);

        // Convert creditCards to CreditCardView list using builder pattern
        List<CreditCardView> convertedCreditCardViews = new ArrayList<>();
        for (CreditCard creditCard : creditCards) {
            CreditCardView view = CreditCardView.builder()
                    .issuanceBank(creditCard.getIssuanceBank())
                    .number(creditCard.getNumber())
                    .build();
            convertedCreditCardViews.add(view);
        }

        return new ResponseEntity<>(convertedCreditCardViews, HttpStatus.OK);
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByNumber(creditCardNumber);
        if (!optionalCreditCard.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(optionalCreditCard.get().getUser().getId(), HttpStatus.OK);
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<String> postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        for (UpdateBalancePayload transaction : payload) {
            Optional<CreditCard> optionalCreditCard = creditCardRepository.findByNumber(transaction.getCreditCardNumber());
            if (!optionalCreditCard.isPresent()) {
                return new ResponseEntity<>("Credit card not found", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Balance updated successfully", HttpStatus.OK);
    }

}
