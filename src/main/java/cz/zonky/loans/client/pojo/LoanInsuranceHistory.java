package cz.zonky.loans.client.pojo;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Michal Svarc
 */
@Getter
@ToString
public class LoanInsuranceHistory {

    private String policyPeriodFrom;
    private String policyPeriodTo;
}
