//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.11.14 at 02:09:16 PM EST 
//


package com.example.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MetricSummaryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MetricSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Sum" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SumOfSquares" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Minimum" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Maximum" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Average" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="StandardDeviation" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetricSummaryType", propOrder = {
		"type",
		"count",
		"sum",
		"sumOfSquares",
		"minimum",
		"maximum",
		"average",
		"standardDeviation"
})
public class MetricSummaryType {

	@XmlElement(name = "Type", required = true)
	protected String type;
	@XmlElement(name = "Count")
	protected int count;
	@XmlElement(name = "Sum")
	protected int sum;
	@XmlElement(name = "SumOfSquares")
	protected int sumOfSquares;
	@XmlElement(name = "Minimum")
	protected int minimum;
	@XmlElement(name = "Maximum")
	protected int maximum;
	@XmlElement(name = "Average")
	protected double average;
	@XmlElement(name = "StandardDeviation")
	protected double standardDeviation;

	/**
	 * Gets the value of the type property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setType(String value) {
		this.type = value;
	}

	/**
	 * Gets the value of the count property.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the value of the count property.
	 */
	public void setCount(int value) {
		this.count = value;
	}

	/**
	 * Gets the value of the sum property.
	 */
	public int getSum() {
		return sum;
	}

	/**
	 * Sets the value of the sum property.
	 */
	public void setSum(int value) {
		this.sum = value;
	}

	/**
	 * Gets the value of the sumOfSquares property.
	 */
	public int getSumOfSquares() {
		return sumOfSquares;
	}

	/**
	 * Sets the value of the sumOfSquares property.
	 */
	public void setSumOfSquares(int value) {
		this.sumOfSquares = value;
	}

	/**
	 * Gets the value of the minimum property.
	 */
	public int getMinimum() {
		return minimum;
	}

	/**
	 * Sets the value of the minimum property.
	 */
	public void setMinimum(int value) {
		this.minimum = value;
	}

	/**
	 * Gets the value of the maximum property.
	 */
	public int getMaximum() {
		return maximum;
	}

	/**
	 * Sets the value of the maximum property.
	 */
	public void setMaximum(int value) {
		this.maximum = value;
	}

	/**
	 * Gets the value of the average property.
	 */
	public double getAverage() {
		return average;
	}

	/**
	 * Sets the value of the average property.
	 */
	public void setAverage(double value) {
		this.average = value;
	}

	/**
	 * Gets the value of the standardDeviation property.
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * Sets the value of the standardDeviation property.
	 */
	public void setStandardDeviation(double value) {
		this.standardDeviation = value;
	}

}