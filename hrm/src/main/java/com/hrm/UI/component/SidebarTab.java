package com.hrm.UI.component;


// model đại diện cho một tab trong sidebar
public class SidebarTab {

    private String title;
    private String cardName;

    public SidebarTab(String title, String cardName) {
        this.title = title;
        this.cardName = cardName;
    }

    public String getTitle() {
        return title;
    }

    public String getCardName() {
        return cardName;
    }
    
}
