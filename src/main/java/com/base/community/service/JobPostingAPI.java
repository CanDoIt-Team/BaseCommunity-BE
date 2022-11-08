package com.base.community.service;


import com.base.community.dto.JobPostingDto;
import com.base.community.model.entity.JobPosting;
import com.base.community.model.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobPostingAPI {
    private final JobPostingRepository jobPostingRepository;

    @Value("${api.external-work.url}")
    String url;

    @Value("${api.external-work.jobCode}")
    String jobCode;

    @Value("${api.external-work.authKey}")
    String authKey;

    @Value("${api.external-work.display}")
    String display;


    public void jobPosting() {
        int result = 0;
        List<JobPostingDto> list = new ArrayList<>();

        try {
            createDocument(list);

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("총 포스팅 갯수" + result);
    }


    private static String getTagValue(String tag, Element eElement) {
        NodeList nList = null;
        Node nValue = null;
        try {
            nList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
            nValue = (Node) nList.item(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }


    public int totalCnt() throws IOException, ParserConfigurationException, SAXException {
        int totCnt = 0;
        String urlBuilder = url + "?authKey=" + authKey + "&callTp=L&returnType=XML&startPage=1&display=1&occupation=" + jobCode;
        Document doc = parsingXML(urlBuilder);
        NodeList nodeList = doc.getElementsByTagName("wantedRoot");
        Node node = nodeList.item(0);
        Element element = (Element) node;

        return totCnt = Integer.parseInt(getTagValue("total", element));
    }


    private void createDocument(List<JobPostingDto> list) throws IOException, ParserConfigurationException, SAXException {
        int pageNum = 0;
        int maxCnt = totalCnt();
        int max = (maxCnt / 100) + 1;

        log.info("############################################");
        log.info(String.valueOf(max));

        for (int i = 1; i <= max; i++) {
            pageNum = i;
            String urlBuilder = url + "?authKey=" + authKey + "&callTp=L&returnType=XML&startPage=" + pageNum + "&display=" + display + "&occupation=" + jobCode;

            Document doc = parsingXML(urlBuilder);


            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("wanted");
            for (int j = 0; j < nodeList.getLength(); j++) {
                Map<String, String> map = new HashMap<>();
                Node node = nodeList.item(j);
                JobPostingDto jobPostingDto = new JobPostingDto();
                Element element = (Element) node;
                Optional<JobPosting> optionalJobPosting = jobPostingRepository.findById(getTagValue("wantedAuthNo", element));

                if (!optionalJobPosting.isPresent()) {
                    log.info("insert 실행");
                    insert(element);
                } else {
                    update(element, optionalJobPosting);
                    log.info("update 실행");

                }
            }
        }
    }


    public Document parsingXML(String urlBuilder) throws IOException, ParserConfigurationException, SAXException {
        String parsingUrl = "";
        URL url = new URL(urlBuilder);
        log.info(url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        log.info(sb.toString());
        parsingUrl = url.toString();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(parsingUrl);


        return doc;
    }


    public void insert(Element element) {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setWantedAuthNo(getTagValue("wantedAuthNo", element));
        jobPosting.setCompany(getTagValue("company", element));
        jobPosting.setBusino(getTagValue("busino", element));
        jobPosting.setTitle(getTagValue("title", element));
        jobPosting.setSalTpNm(getTagValue("salTpNm", element));
        jobPosting.setSal(getTagValue("sal", element));
        jobPosting.setMinSal(getTagValue("minSal", element));
        jobPosting.setRegion(getTagValue("region", element));
        jobPosting.setHolidayTpNm(getTagValue("holidayTpNm", element));
        jobPosting.setMinEdubg(getTagValue("minEdubg", element));
        jobPosting.setCareer(getTagValue("career", element));
        jobPosting.setRegDt(getTagValue("regDt", element));
        jobPosting.setCloseDt(getTagValue("closeDt", element));
        jobPosting.setInfoSvc(getTagValue("infoSvc", element));
        jobPosting.setWantedInfoUrl(getTagValue("wantedInfoUrl", element));
        jobPosting.setWantedMobileInfoUrl(getTagValue("wantedMobileInfoUrl", element));
        jobPosting.setZipCd(getTagValue("zipCd", element));
        jobPosting.setStrtnmCd(getTagValue("strtnmCd", element));
        jobPosting.setBasicAddr(getTagValue("basicAddr", element));
        jobPosting.setDetailAddr(getTagValue("detailAddr", element));
        jobPosting.setEmpTpCd(getTagValue("empTpCd", element));
        jobPosting.setJobsCd(getTagValue("jobsCd", element));
        jobPosting.setSmodifyDtm(getTagValue("smodifyDtm", element));
        jobPosting.setPrefCd(getTagValue("prefCd", element));
        jobPostingRepository.save(jobPosting);

    }

    public void update(Element element, Optional<JobPosting> optionalJobPosting) {

        JobPosting jobPosting = optionalJobPosting.get();
        jobPosting.setCompany(getTagValue("company", element));
        jobPosting.setBusino(getTagValue("busino", element));
        jobPosting.setTitle(getTagValue("title", element));
        jobPosting.setSalTpNm(getTagValue("salTpNm", element));
        jobPosting.setSal(getTagValue("sal", element));
        jobPosting.setMinSal(getTagValue("minSal", element));
        jobPosting.setRegion(getTagValue("region", element));
        jobPosting.setHolidayTpNm(getTagValue("holidayTpNm", element));
        jobPosting.setMinEdubg(getTagValue("minEdubg", element));
        jobPosting.setCareer(getTagValue("career", element));
        jobPosting.setRegDt(getTagValue("regDt", element));
        jobPosting.setCloseDt(getTagValue("closeDt", element));
        jobPosting.setInfoSvc(getTagValue("infoSvc", element));
        jobPosting.setWantedInfoUrl(getTagValue("wantedInfoUrl", element));
        jobPosting.setWantedMobileInfoUrl(getTagValue("wantedMobileInfoUrl", element));
        jobPosting.setZipCd(getTagValue("zipCd", element));
        jobPosting.setStrtnmCd(getTagValue("strtnmCd", element));
        jobPosting.setBasicAddr(getTagValue("basicAddr", element));
        jobPosting.setDetailAddr(getTagValue("detailAddr", element));
        jobPosting.setEmpTpCd(getTagValue("empTpCd", element));
        jobPosting.setJobsCd(getTagValue("jobsCd", element));
        jobPosting.setSmodifyDtm(getTagValue("smodifyDtm", element));
        jobPosting.setPrefCd(getTagValue("prefCd", element));
        jobPostingRepository.save(jobPosting);
    }


    public void deleteJobPosting() throws IOException, ParserConfigurationException, SAXException {
        long count = jobPostingRepository.getCount();
        List<String> name = jobPostingRepository.getNo();
        List<String> apiList = deleteDocument();

        for (int i = 0; i < count; i++) {
            String findNo = name.get(i);
            List<String> result = apiList.stream()
                    .filter(str -> str.trim().equals(findNo))
                    .collect(Collectors.toList());
            if (result.size() == 0) {
                jobPostingRepository.deleteById(findNo);
                log.info("#######################################");
                log.info(findNo + "삭제");
                log.info("######################################3");
            }
        }
    }

    private List<String> deleteDocument() throws IOException, ParserConfigurationException, SAXException {
        int maxCnt = totalCnt();
        int max = (maxCnt / 100) + 1;
        int pageNum = 0;
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= max; i++) {
            pageNum = i;
            String urlBuilder = url + "?authKey=" + authKey + "&callTp=L&returnType=XML&startPage=" + pageNum + "&display=" + display + "&occupation=" + jobCode;

            Document doc = parsingXML(urlBuilder);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("wanted");
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                JobPostingDto jobPostingDto = new JobPostingDto();
                Element element = (Element) node;
                list.add(getTagValue("wantedAuthNo", element));
            }

        }
        return list;
    }


}

