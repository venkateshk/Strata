/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * <p>
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.credit.general;

import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.finance.credit.REDCode;
import com.opengamma.strata.finance.credit.general.reference.IndexReferenceInformation;
import com.opengamma.strata.finance.credit.general.reference.ReferenceInformation;
import com.opengamma.strata.finance.credit.general.reference.SeniorityLevel;
import com.opengamma.strata.finance.credit.general.reference.SingleNameReferenceInformation;
import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This element contains all the data that appears in the section entitled "1. General Terms"
 * in the 2003 ISDA Credit Derivatives Confirmation
 */
@BeanDefinition
public final class GeneralTerms
    implements ImmutableBean, Serializable {

  /**
   * The first day of the term of the trade. This day may be subject to adjustment in accordance
   * with a business day convention. ISDA 2003 Term: Effective Date
   */
  @PropertyDefinition(validate = "notNull")
  final LocalDate effectiveDate;

  /**
   * The scheduled date on which the credit protection will lapse. This day may be subject to
   * adjustment in accordance with a business day convention. ISDA 2003 Term: Scheduled Termination Date.
   */
  @PropertyDefinition(validate = "notNull")
  final LocalDate scheduledTerminationDate;

  /**
   * The purpose of this element is to disambiguate whether the buyer of the product effectively buys
   * protection or whether he buys risk (and, hence, sells protection) in the case, such as high yields
   * instruments, where no firm standard appears to exist at the execution level.
   */
  @PropertyDefinition(validate = "notNull")
  final BuyerConvention buyerConvention;

  /**
   * ISDA 2003 Terms: Business Day and Business Day Convention
   */
  @PropertyDefinition(validate = "notNull")
  final BusinessDayAdjustment dateAdjustments;

  /**
   * Contains information on reference entity/issue for single name or
   * index information for index trades
   */
  @PropertyDefinition(validate = "notNull")
  final ReferenceInformation referenceInformation;

  public static GeneralTerms singleName(
      LocalDate effectiveDate,
      LocalDate scheduledTerminationDate,
      BuyerConvention buyerConvention,
      BusinessDayAdjustment businessDayAdjustment,
      REDCode referenceEntityId,
      String referenceEntityName,
      Currency currency,
      SeniorityLevel seniority
  ) {
    return GeneralTerms
        .builder()
        .effectiveDate(effectiveDate)
        .scheduledTerminationDate(scheduledTerminationDate)
        .buyerConvention(buyerConvention)
        .dateAdjustments(businessDayAdjustment)
        .referenceInformation(
            SingleNameReferenceInformation
                .builder()
                .referenceEntityName(referenceEntityName)
                .referenceEntityId(referenceEntityId)
                .currency(currency)
                .seniority(seniority)
                .build()
        )
        .build();
  }

  public static GeneralTerms index(
      LocalDate effectiveDate,
      LocalDate scheduledTerminationDate,
      BuyerConvention buyerConvention,
      BusinessDayAdjustment businessDayAdjustment,
      REDCode indexId,
      String indexName,
      int indexSeries,
      int indexAnnexVersion
  ) {
    return GeneralTerms
        .builder()
        .effectiveDate(effectiveDate)
        .scheduledTerminationDate(scheduledTerminationDate)
        .buyerConvention(buyerConvention)
        .dateAdjustments(businessDayAdjustment)
        .referenceInformation(
            IndexReferenceInformation
                .builder()
                .indexId(indexId)
                .indexName(indexName)
                .indexSeries(indexSeries)
                .indexAnnexVersion(indexAnnexVersion)
                .build()
        )
        .build();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code GeneralTerms}.
   * @return the meta-bean, not null
   */
  public static GeneralTerms.Meta meta() {
    return GeneralTerms.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(GeneralTerms.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static GeneralTerms.Builder builder() {
    return new GeneralTerms.Builder();
  }

  private GeneralTerms(
      LocalDate effectiveDate,
      LocalDate scheduledTerminationDate,
      BuyerConvention buyerConvention,
      BusinessDayAdjustment dateAdjustments,
      ReferenceInformation referenceInformation) {
    JodaBeanUtils.notNull(effectiveDate, "effectiveDate");
    JodaBeanUtils.notNull(scheduledTerminationDate, "scheduledTerminationDate");
    JodaBeanUtils.notNull(buyerConvention, "buyerConvention");
    JodaBeanUtils.notNull(dateAdjustments, "dateAdjustments");
    JodaBeanUtils.notNull(referenceInformation, "referenceInformation");
    this.effectiveDate = effectiveDate;
    this.scheduledTerminationDate = scheduledTerminationDate;
    this.buyerConvention = buyerConvention;
    this.dateAdjustments = dateAdjustments;
    this.referenceInformation = referenceInformation;
  }

  @Override
  public GeneralTerms.Meta metaBean() {
    return GeneralTerms.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the first day of the term of the trade. This day may be subject to adjustment in accordance
   * with a business day convention. ISDA 2003 Term: Effective Date
   * @return the value of the property, not null
   */
  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the scheduled date on which the credit protection will lapse. This day may be subject to
   * adjustment in accordance with a business day convention. ISDA 2003 Term: Scheduled Termination Date.
   * @return the value of the property, not null
   */
  public LocalDate getScheduledTerminationDate() {
    return scheduledTerminationDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the purpose of this element is to disambiguate whether the buyer of the product effectively buys
   * protection or whether he buys risk (and, hence, sells protection) in the case, such as high yields
   * instruments, where no firm standard appears to exist at the execution level.
   * @return the value of the property, not null
   */
  public BuyerConvention getBuyerConvention() {
    return buyerConvention;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets iSDA 2003 Terms: Business Day and Business Day Convention
   * @return the value of the property, not null
   */
  public BusinessDayAdjustment getDateAdjustments() {
    return dateAdjustments;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets contains information on reference entity/issue for single name or
   * index information for index trades
   * @return the value of the property, not null
   */
  public ReferenceInformation getReferenceInformation() {
    return referenceInformation;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      GeneralTerms other = (GeneralTerms) obj;
      return JodaBeanUtils.equal(getEffectiveDate(), other.getEffectiveDate()) &&
          JodaBeanUtils.equal(getScheduledTerminationDate(), other.getScheduledTerminationDate()) &&
          JodaBeanUtils.equal(getBuyerConvention(), other.getBuyerConvention()) &&
          JodaBeanUtils.equal(getDateAdjustments(), other.getDateAdjustments()) &&
          JodaBeanUtils.equal(getReferenceInformation(), other.getReferenceInformation());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getEffectiveDate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getScheduledTerminationDate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getBuyerConvention());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDateAdjustments());
    hash = hash * 31 + JodaBeanUtils.hashCode(getReferenceInformation());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("GeneralTerms{");
    buf.append("effectiveDate").append('=').append(getEffectiveDate()).append(',').append(' ');
    buf.append("scheduledTerminationDate").append('=').append(getScheduledTerminationDate()).append(',').append(' ');
    buf.append("buyerConvention").append('=').append(getBuyerConvention()).append(',').append(' ');
    buf.append("dateAdjustments").append('=').append(getDateAdjustments()).append(',').append(' ');
    buf.append("referenceInformation").append('=').append(JodaBeanUtils.toString(getReferenceInformation()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code GeneralTerms}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code effectiveDate} property.
     */
    private final MetaProperty<LocalDate> effectiveDate = DirectMetaProperty.ofImmutable(
        this, "effectiveDate", GeneralTerms.class, LocalDate.class);
    /**
     * The meta-property for the {@code scheduledTerminationDate} property.
     */
    private final MetaProperty<LocalDate> scheduledTerminationDate = DirectMetaProperty.ofImmutable(
        this, "scheduledTerminationDate", GeneralTerms.class, LocalDate.class);
    /**
     * The meta-property for the {@code buyerConvention} property.
     */
    private final MetaProperty<BuyerConvention> buyerConvention = DirectMetaProperty.ofImmutable(
        this, "buyerConvention", GeneralTerms.class, BuyerConvention.class);
    /**
     * The meta-property for the {@code dateAdjustments} property.
     */
    private final MetaProperty<BusinessDayAdjustment> dateAdjustments = DirectMetaProperty.ofImmutable(
        this, "dateAdjustments", GeneralTerms.class, BusinessDayAdjustment.class);
    /**
     * The meta-property for the {@code referenceInformation} property.
     */
    private final MetaProperty<ReferenceInformation> referenceInformation = DirectMetaProperty.ofImmutable(
        this, "referenceInformation", GeneralTerms.class, ReferenceInformation.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "effectiveDate",
        "scheduledTerminationDate",
        "buyerConvention",
        "dateAdjustments",
        "referenceInformation");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -930389515:  // effectiveDate
          return effectiveDate;
        case -1325141915:  // scheduledTerminationDate
          return scheduledTerminationDate;
        case -1550125692:  // buyerConvention
          return buyerConvention;
        case 1942192152:  // dateAdjustments
          return dateAdjustments;
        case -2117930783:  // referenceInformation
          return referenceInformation;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public GeneralTerms.Builder builder() {
      return new GeneralTerms.Builder();
    }

    @Override
    public Class<? extends GeneralTerms> beanType() {
      return GeneralTerms.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code effectiveDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> effectiveDate() {
      return effectiveDate;
    }

    /**
     * The meta-property for the {@code scheduledTerminationDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> scheduledTerminationDate() {
      return scheduledTerminationDate;
    }

    /**
     * The meta-property for the {@code buyerConvention} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BuyerConvention> buyerConvention() {
      return buyerConvention;
    }

    /**
     * The meta-property for the {@code dateAdjustments} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BusinessDayAdjustment> dateAdjustments() {
      return dateAdjustments;
    }

    /**
     * The meta-property for the {@code referenceInformation} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ReferenceInformation> referenceInformation() {
      return referenceInformation;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -930389515:  // effectiveDate
          return ((GeneralTerms) bean).getEffectiveDate();
        case -1325141915:  // scheduledTerminationDate
          return ((GeneralTerms) bean).getScheduledTerminationDate();
        case -1550125692:  // buyerConvention
          return ((GeneralTerms) bean).getBuyerConvention();
        case 1942192152:  // dateAdjustments
          return ((GeneralTerms) bean).getDateAdjustments();
        case -2117930783:  // referenceInformation
          return ((GeneralTerms) bean).getReferenceInformation();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code GeneralTerms}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<GeneralTerms> {

    private LocalDate effectiveDate;
    private LocalDate scheduledTerminationDate;
    private BuyerConvention buyerConvention;
    private BusinessDayAdjustment dateAdjustments;
    private ReferenceInformation referenceInformation;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(GeneralTerms beanToCopy) {
      this.effectiveDate = beanToCopy.getEffectiveDate();
      this.scheduledTerminationDate = beanToCopy.getScheduledTerminationDate();
      this.buyerConvention = beanToCopy.getBuyerConvention();
      this.dateAdjustments = beanToCopy.getDateAdjustments();
      this.referenceInformation = beanToCopy.getReferenceInformation();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -930389515:  // effectiveDate
          return effectiveDate;
        case -1325141915:  // scheduledTerminationDate
          return scheduledTerminationDate;
        case -1550125692:  // buyerConvention
          return buyerConvention;
        case 1942192152:  // dateAdjustments
          return dateAdjustments;
        case -2117930783:  // referenceInformation
          return referenceInformation;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -930389515:  // effectiveDate
          this.effectiveDate = (LocalDate) newValue;
          break;
        case -1325141915:  // scheduledTerminationDate
          this.scheduledTerminationDate = (LocalDate) newValue;
          break;
        case -1550125692:  // buyerConvention
          this.buyerConvention = (BuyerConvention) newValue;
          break;
        case 1942192152:  // dateAdjustments
          this.dateAdjustments = (BusinessDayAdjustment) newValue;
          break;
        case -2117930783:  // referenceInformation
          this.referenceInformation = (ReferenceInformation) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public GeneralTerms build() {
      return new GeneralTerms(
          effectiveDate,
          scheduledTerminationDate,
          buyerConvention,
          dateAdjustments,
          referenceInformation);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code effectiveDate} property in the builder.
     * @param effectiveDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder effectiveDate(LocalDate effectiveDate) {
      JodaBeanUtils.notNull(effectiveDate, "effectiveDate");
      this.effectiveDate = effectiveDate;
      return this;
    }

    /**
     * Sets the {@code scheduledTerminationDate} property in the builder.
     * @param scheduledTerminationDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder scheduledTerminationDate(LocalDate scheduledTerminationDate) {
      JodaBeanUtils.notNull(scheduledTerminationDate, "scheduledTerminationDate");
      this.scheduledTerminationDate = scheduledTerminationDate;
      return this;
    }

    /**
     * Sets the {@code buyerConvention} property in the builder.
     * @param buyerConvention  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder buyerConvention(BuyerConvention buyerConvention) {
      JodaBeanUtils.notNull(buyerConvention, "buyerConvention");
      this.buyerConvention = buyerConvention;
      return this;
    }

    /**
     * Sets the {@code dateAdjustments} property in the builder.
     * @param dateAdjustments  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder dateAdjustments(BusinessDayAdjustment dateAdjustments) {
      JodaBeanUtils.notNull(dateAdjustments, "dateAdjustments");
      this.dateAdjustments = dateAdjustments;
      return this;
    }

    /**
     * Sets the {@code referenceInformation} property in the builder.
     * @param referenceInformation  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder referenceInformation(ReferenceInformation referenceInformation) {
      JodaBeanUtils.notNull(referenceInformation, "referenceInformation");
      this.referenceInformation = referenceInformation;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(192);
      buf.append("GeneralTerms.Builder{");
      buf.append("effectiveDate").append('=').append(JodaBeanUtils.toString(effectiveDate)).append(',').append(' ');
      buf.append("scheduledTerminationDate").append('=').append(JodaBeanUtils.toString(scheduledTerminationDate)).append(',').append(' ');
      buf.append("buyerConvention").append('=').append(JodaBeanUtils.toString(buyerConvention)).append(',').append(' ');
      buf.append("dateAdjustments").append('=').append(JodaBeanUtils.toString(dateAdjustments)).append(',').append(' ');
      buf.append("referenceInformation").append('=').append(JodaBeanUtils.toString(referenceInformation));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
