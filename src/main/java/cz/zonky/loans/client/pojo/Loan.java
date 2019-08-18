package cz.zonky.loans.client.pojo;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author Michal Svarc
 */
@Getter
@ToString
public class Loan {

    private Long id;
    private String url;
    private String name;
    private String story;
    private LoanPurpose purpose;
    private List<LoanPhoto> photos;
    private String nickName;
    private Long termInMonths;
    private Double interestRate;
    private Double revenueRate;
    private Long annuityWithInsurance;
    private String rating;
    private Boolean topped;
    private Long amount;
    private String currency;
    private Long remainingInvestment;
    private Long reservedAmount;
    private Double investmentRate;
    private Boolean covered;
    private String datePublished;
    private Boolean published;
    private String deadline;
    private Long investmentsCount;
    private Long questionsCount;
    private String region;
    private String mainIncomeType;
    private Boolean insuranceActive;
    private List<LoanInsuranceHistory> insuranceHistory;
    // Missing in Apiary - not always sure of field type (it is not described and value is sometimes almost always null)
    private Long userId;
    private Double annuity;
    private Boolean premium;
    private Double zonkyPlusAmount;
    private String myOtherInvestments;
    private String borrowerRelatedInvestmentInfo;
    private Boolean questionsAllowed;
    private Long activeLoansCount;
    private Boolean fastcash;
    private Boolean multicash;
    private Boolean insuredInFuture;
}
