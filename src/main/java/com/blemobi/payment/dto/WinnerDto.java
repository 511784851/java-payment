/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.dto
 *
 *    Filename:    WinnerDto.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年4月12日 下午8:54:37
 *
 *    Revision:
 *
 *    2017年4月12日 下午8:54:37
 *
 *****************************************************************/
package com.blemobi.payment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName WinnerDto
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年4月12日 下午8:54:37
 * @version 1.0.0
 */
@Setter
@Getter
@ToString
public class WinnerDto {
    private String uuid;
    private String headUrl;
    private String nickNm;
    private Integer level;
}
