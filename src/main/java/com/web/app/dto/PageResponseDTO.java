package com.web.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    // 시작 페이지 번호
    private int start;
    // 끝 페이지 번호
    private int end;

    // 이전 페이지의 존재 여부
    private boolean prev;
    // 다음 페이지의 존재 여부
    private boolean next;

    private List<E> dtoList;

    @Builder
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.dtoList = dtoList;
        this.total = total;

        this.end = (int)(Math.ceil(this.page / 10.0)) * 10;

        this.start = end - 9;

        int last = (int)(Math.ceil(total/(double) size));

        this.end = Math.min(end, last);

        this.prev = this.start > 1;

        this.next = total > this.end * this.size;

    }



}
