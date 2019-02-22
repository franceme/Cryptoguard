//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.18 at 09:52:45 PM EST 
//


package com.example.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * The root tag AnalyzerReport can have a child element
 * with BugSummary tag. There can be only one occurrence of
 * BugSummary.
 *
 *
 * <p>Java class for BugSummaryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BugSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BugCategory" type="{https://www.swamp.com/com/scarf/struct}BugCategoryType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author drmonster
 * @version $Id: $Id
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BugSummaryType", propOrder = {
        "bugCategory"
})
public class BugSummaryType {

    @XmlElement(name = "BugCategory", required = true)
    protected List<BugCategoryType> bugCategory;

    /**
     * Gets the value of the bugCategory property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bugCategory property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBugCategory().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link com.example.response.BugCategoryType}
     *
     * @return a {@link java.util.List} object.
     */
    public List<BugCategoryType> getBugCategory() {
        if (bugCategory == null) {
            bugCategory = new ArrayList<BugCategoryType>();
        }
        return this.bugCategory;
    }

}
