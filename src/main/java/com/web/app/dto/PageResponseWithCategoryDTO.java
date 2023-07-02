package com.web.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class PageResponseWithCategoryDTO<E> {

    private int page;
    private int size;
    private int total; // 검색 & 필터링 전 총 게시글 개수
    private long filteredTotal; // 검색 & 필터링된 게시글 개수

    // 시작 페이지 번호
    private int start;
    // 끝 페이지 번호
    private int end;

    // 이전 페이지의 존재 여부
    private boolean prev;
    // 다음 페이지의 존재 여부
    private boolean next;

    private List<E> dtoList;

    private Map<String, Integer> dtoTags;

    @Builder(builderMethodName = "of", builderClassName = "of")
    private PageResponseWithCategoryDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total, Map<String, Integer> dtoTags, long filteredTotal) {

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.dtoList = dtoList;
        this.total = total;
        this.dtoTags = dtoTags;
        this.filteredTotal = filteredTotal;

        this.end = (int) (Math.ceil(this.page / 10.0)) * 10;

        this.start = end - 9;

        int last = 0;
        if (total != filteredTotal) { // 검색 || 필터링 조회
            last = (int) (Math.ceil(filteredTotal / (double) size));
        } else { // 전체 조회
            last = (int) (Math.ceil(total / (double) size));
        }

        this.end = Math.min(end, last);

        this.prev = this.start > 1;

        if (total != filteredTotal) { // 검색 || 필터링 조회
            this.next = filteredTotal > this.end * this.size;
        } else { // 전체 조회
            this.next = total > this.end * this.size;
        }
    }

    @Builder(builderMethodName = "withAll")
    public static PageResponseWithCategoryDTO builderByAll(PageRequestDTO pageRequestDTO, List<BoardDTO> dtoList, int total, Map<String, Integer> dtoTags) {

        return PageResponseWithCategoryDTO.<BoardDTO>of()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .dtoTags(dtoTags)
                .filteredTotal(total).build();
    }

    @Builder(builderMethodName = "withFilter")
    public static PageResponseWithCategoryDTO builderByFilter(PageRequestDTO pageRequestDTO, List<BoardDTO> dtoList, int total, Map<String, Integer> dtoTags, long filteredTotal) {

        return PageResponseWithCategoryDTO.<BoardDTO>of()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .dtoTags(dtoTags)
                .filteredTotal(filteredTotal).build();
    }

}
