//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.18 at 09:52:45 PM EST 
//


package com.example.response;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for InstanceLocationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InstanceLocationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Xpath" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *         &lt;element name="LineNum" type="{https://www.swamp.com/com/scarf/struct}LineNumType" minOccurs="0"/>
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
@XmlType(name = "InstanceLocationType", propOrder = {
        "xpath",
        "lineNum"
})
public class InstanceLocationType {

    @XmlElement(name = "Xpath")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String xpath;
    @XmlElement(name = "LineNum")
    protected LineNumType lineNum;

    /**
     * Gets the value of the xpath property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getXpath() {
        return xpath;
    }

    /**
     * Sets the value of the xpath property.
     *
     * @param value allowed object is
     *              {@link java.lang.String}
     */
    public void setXpath(String value) {
        this.xpath = value;
    }

    /**
     * Gets the value of the lineNum property.
     *
     * @return a {@link com.example.response.LineNumType} object.
     */
    public LineNumType getLineNum() {
        return lineNum;
    }

    /**
     * Sets the value of the lineNum property.
     *
     * @param value allowed object is
     *              {@link com.example.response.LineNumType}
     */
    public void setLineNum(LineNumType value) {
        this.lineNum = value;
    }

}
