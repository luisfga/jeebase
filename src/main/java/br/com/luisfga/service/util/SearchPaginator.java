package br.com.luisfga.service.util;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.TypedQuery;

@Stateful
public class SearchPaginator<T> implements Serializable{
    
    private int pageIndex = 0;
    private int pageSize = 10;
    private long count;
    private int lastPage;
    private Integer[] pagesLabels;

    private List<T> list;
    private TypedQuery<T> typedQuery;
    
    public int getPageIndex() {
        return pageIndex;
    }
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        recomputePages();
    }
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
        recomputePages();
    }
    public int getLastPage() {
        return lastPage;
    }
    public Integer[] getPagesLabels() {
        return pagesLabels;
    }
    public void setPagesLabels(Integer[] pagesLabels) {
        this.pagesLabels = pagesLabels;
    }

    
    public void setList(List<T> list) {
        this.list = list;
    }
    public List<T> getList() {
        return list;
    }
    public TypedQuery<T> getTypedQuery() {
        return typedQuery;
    }
    public void setTypedQuery(TypedQuery<T> typedQuery) {
        this.pageIndex = 0;
        this.typedQuery = typedQuery;
        goToPage();
    }

    public void recomputePages(){
        //"ceil" to add a extra page for the rest of the division, when needed
        this.lastPage = (int) Math.ceil((float)this.count / pageSize);
        
        //set pages labels only if needed
        if(this.lastPage > 1) {
            this.pagesLabels = new Integer[this.lastPage];
            for (int i = 0; i < this.lastPage; i++) {
                this.pagesLabels[i] = i; //<- pageIndex number, need add +1 on view to start from 1
            }
        }
    }
    
    public void previousPage(){
        pageIndex--;
        goToPage();
    }
    public void nextPage(){
        pageIndex++;
        goToPage();
    }
    private void goToPage(){
        list = typedQuery.setFirstResult(pageIndex*pageSize).setMaxResults(pageSize).getResultList();
    }
    public boolean currentPageIsFirst(){
        return this.pageIndex == 0;
    }
    public boolean currentPageIsLast(){
        return this.pageIndex == this.lastPage-1;
    }    

    public void goToFirstPage(){
        this.pageIndex = 0;
        goToPage();
    }
    public void goToPreviousPage(){
        this.pageIndex--;
        goToPage();
    }
    public void goToNextPage(){
        this.pageIndex++;
        goToPage();
    }
    public void goToLastPage(){
        this.pageIndex = this.lastPage-1;
        goToPage();
    }
    public Integer currentPageNumber(){
        return this.pageIndex+1;
    }
}
