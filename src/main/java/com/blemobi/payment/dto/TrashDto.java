/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.dto
 *
 *    Filename:    TrashDto.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年4月12日 下午8:53:43
 *
 *    Revision:
 *
 *    2017年4月12日 下午8:53:43
 *
 *****************************************************************/
package com.blemobi.payment.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName TrashDto
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年4月12日 下午8:53:43
 * @version 1.0.0
 */
@Setter
@Getter
@ToString
public class TrashDto {
    private String lotteryId;
    private Integer type;
    private List<WinnerDto> winnerList;
    private Long crtTm;
    private String title;
    private Long delTm;
    private String operator;
    private String desc;
    private Integer winnerCnt;
}
