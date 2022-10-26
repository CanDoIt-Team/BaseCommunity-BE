package com.base.community.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDto {

    private String wantedAuthNo;	//구인인증번호
    private String company; //회사명
    private String busino; //사업자등록번호
    private String title;		//채용제목
    private String salTpNm;		//임금형태
    private String sal;		//급여
    private String minSal;		//최소임금액
    private String maxSal;		//최대임금액
    private String region;		//근무지역
    private String holidayTpNm;		//근무형태
    private String minEdubg;		//최소학력
    private String maxEdubg;		//최대학력
    private String career;		//경력
    private String regDt;		//등록일자
    private String closeDt;		//종료일자
    private String infoSvc;		//정보제공처(VALIDATION 워크넷 인증)
    private String wantedInfoUrl;	//워크넷 채용정보 URL
    private String wantedMobileInfoUrl; //워크넷 모바일 채용정보 URL
    private String zipCd; //근무지 우편주소
    private String strtnmCd;	//근무지 도로명주소
    private String basicAddr;	//근무지 기본주소
    private String detailAddr;	//근무지 상세주소
    private Long empTpCd;		//고용형태코드
    private Long jobsCd;		//직종코드
    private Long smodifyDtm;	//최종수정일
    private String prefCd;	//우대조건

}
