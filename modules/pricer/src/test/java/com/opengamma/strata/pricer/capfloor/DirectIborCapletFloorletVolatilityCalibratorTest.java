package com.opengamma.strata.pricer.capfloor;

import static com.opengamma.strata.basics.date.DayCounts.ACT_ACT_ISDA;
import static com.opengamma.strata.basics.index.IborIndices.USD_LIBOR_3M;
import static org.testng.Assert.assertEquals;

import java.time.Period;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.collect.array.DoubleMatrix;
import com.opengamma.strata.collect.tuple.Pair;
import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.curve.interpolator.CurveInterpolators;
import com.opengamma.strata.market.surface.ConstantSurface;
import com.opengamma.strata.market.surface.Surface;
import com.opengamma.strata.market.surface.Surfaces;
import com.opengamma.strata.market.surface.interpolator.GridSurfaceInterpolator;
import com.opengamma.strata.pricer.option.RawOptionData;
import com.opengamma.strata.product.capfloor.ResolvedIborCapFloorLeg;

@Test
public class DirectIborCapletFloorletVolatilityCalibratorTest
    extends CapletStrippingSetup {

  private static final DirectIborCapletFloorletVolatilityCalibrator CALIBRATOR =
      DirectIborCapletFloorletVolatilityCalibrator.DEFAULT;
  private static final BlackIborCapFloorLegPricer LEG_PRICER_BLACK = BlackIborCapFloorLegPricer.DEFAULT;
  private static final NormalIborCapFloorLegPricer LEG_PRICER_NORMAL = NormalIborCapFloorLegPricer.DEFAULT;
  private static final double TOL = 1.0e-4;

  public void test_recovery_black() {

    double lambdaT = 0.07;
    double lambdaK = 0.07;
    double error = 1.0e-5;


    DirectIborCapletFloorletVolatilityDefinition definition = DirectIborCapletFloorletVolatilityDefinition.of(
        IborCapletFloorletVolatilitiesName.of("test"), USD_LIBOR_3M, ACT_ACT_ISDA, lambdaT, lambdaK,
        GridSurfaceInterpolator.of(CurveInterpolators.LINEAR, CurveInterpolators.LINEAR));
    ImmutableList<Period> maturities = createBlackMaturities();
    DoubleArray strikes = createBlackStrikes();
    RawOptionData data = RawOptionData.of(
        maturities, strikes, ValueType.STRIKE, createFullBlackDataMatrix(),
        DoubleMatrix.filled(maturities.size(), strikes.size(), error), ValueType.BLACK_VOLATILITY);
    IborCapletFloorletVolatilityCalibrationResult res = CALIBRATOR.calibrate(definition, CALIBRATION_TIME, data, RATES_PROVIDER);
    BlackIborCapletFloorletExpiryStrikeVolatilities resVols =
        (BlackIborCapletFloorletExpiryStrikeVolatilities) res.getVolatilities();
    for (int i = 0; i < NUM_BLACK_STRIKES; ++i) {
      Pair<List<ResolvedIborCapFloorLeg>, List<Double>> capsAndVols = getCapsBlackVols(i);
      List<ResolvedIborCapFloorLeg> caps = capsAndVols.getFirst();
      List<Double> vols = capsAndVols.getSecond();
      int nCaps = caps.size();
      for (int j = 0; j < nCaps; ++j) {
        ConstantSurface volSurface = ConstantSurface.of(
            Surfaces.blackVolatilityByExpiryStrike("test", ACT_ACT_ISDA), vols.get(j));
        BlackIborCapletFloorletExpiryStrikeVolatilities constVol = BlackIborCapletFloorletExpiryStrikeVolatilities.of(
            USD_LIBOR_3M, CALIBRATION_TIME, volSurface);
        double priceOrg = LEG_PRICER_BLACK.presentValue(caps.get(j), RATES_PROVIDER, constVol).getAmount();
        double priceCalib = LEG_PRICER_BLACK.presentValue(caps.get(j), RATES_PROVIDER, resVols).getAmount();
        assertEquals(priceOrg, priceCalib, Math.max(priceOrg, 1d) * TOL * 5d);
      }
    }

//    print(res, strikes, 10d);
//    assertTrue(res.getChiSquare() > 0d);
//    assertEquals(resVols.getIndex(), USD_LIBOR_3M);
//    assertEquals(resVols.getName(), definition.getName());
//    assertEquals(resVols.getValuationDateTime(), CALIBRATION_TIME);
//    assertEquals(resVols.getParameters().getShiftCurve(), definition.getShiftCurve());
//    assertEquals(resVols.getParameters().getBetaCurve(), definition.getBetaCurve().get());
  }

  public void recovery_test_shiftedBlack() {

    double lambdaT = 0.07;
    double lambdaK = 0.07;
    double error = 1.0e-5;

    DirectIborCapletFloorletVolatilityDefinition definition = DirectIborCapletFloorletVolatilityDefinition.of(
        IborCapletFloorletVolatilitiesName.of("test"), USD_LIBOR_3M, ACT_ACT_ISDA, lambdaT, lambdaK,
        GridSurfaceInterpolator.of(CurveInterpolators.LINEAR, CurveInterpolators.LINEAR), 0.02);
    ImmutableList<Period> maturities = createBlackMaturities();
    DoubleArray strikes = createBlackStrikes();
    RawOptionData data = RawOptionData.of(
        maturities, strikes, ValueType.STRIKE, createFullBlackDataMatrix(),
        DoubleMatrix.filled(maturities.size(), strikes.size(), error), ValueType.BLACK_VOLATILITY);
    IborCapletFloorletVolatilityCalibrationResult res = CALIBRATOR.calibrate(definition, CALIBRATION_TIME, data, RATES_PROVIDER);
    ShiftedBlackIborCapletFloorletExpiryStrikeVolatilities resVols =
        (ShiftedBlackIborCapletFloorletExpiryStrikeVolatilities) res.getVolatilities();
    for (int i = 0; i < NUM_BLACK_STRIKES; ++i) {
      Pair<List<ResolvedIborCapFloorLeg>, List<Double>> capsAndVols = getCapsBlackVols(i);
      List<ResolvedIborCapFloorLeg> caps = capsAndVols.getFirst();
      List<Double> vols = capsAndVols.getSecond();
      int nCaps = caps.size();
      for (int j = 0; j < nCaps; ++j) {
        ConstantSurface volSurface = ConstantSurface.of(
            Surfaces.blackVolatilityByExpiryStrike("test", ACT_ACT_ISDA), vols.get(j));
        BlackIborCapletFloorletExpiryStrikeVolatilities constVol = BlackIborCapletFloorletExpiryStrikeVolatilities.of(
            USD_LIBOR_3M, CALIBRATION_TIME, volSurface);
        double priceOrg = LEG_PRICER_BLACK.presentValue(caps.get(j), RATES_PROVIDER, constVol).getAmount();
        double priceCalib = LEG_PRICER_BLACK.presentValue(caps.get(j), RATES_PROVIDER, resVols).getAmount();
        assertEquals(priceOrg, priceCalib, Math.max(priceOrg, 1d) * TOL);
      }
    }

//    assertTrue(res.getChiSquare() > 0d);
//    assertEquals(resVols.getIndex(), USD_LIBOR_3M);
//    assertEquals(resVols.getName(), definition.getName());
//    assertEquals(resVols.getValuationDateTime(), CALIBRATION_TIME);
//    assertEquals(resVols.getParameters().getShiftCurve(), definition.getShiftCurve());
//    assertEquals(resVols.getParameters().getBetaCurve(), definition.getBetaCurve().get());
  }

  public void recovery_test_flat() {
    double lambdaT = 0.01;
    double lambdaK = 0.01;
    double error = 1.0e-3;
    DirectIborCapletFloorletVolatilityDefinition definition = DirectIborCapletFloorletVolatilityDefinition.of(
        IborCapletFloorletVolatilitiesName.of("test"), USD_LIBOR_3M, ACT_ACT_ISDA, lambdaT, lambdaK,
        GridSurfaceInterpolator.of(CurveInterpolators.LINEAR, CurveInterpolators.LINEAR));
    ImmutableList<Period> maturities = createBlackMaturities();
    DoubleArray strikes = createBlackStrikes();
    RawOptionData data = RawOptionData.of(
        maturities, strikes, ValueType.STRIKE, createFullFlatBlackDataMatrix(),
        DoubleMatrix.filled(maturities.size(), strikes.size(), error), ValueType.BLACK_VOLATILITY);
    IborCapletFloorletVolatilityCalibrationResult res = CALIBRATOR.calibrate(definition, CALIBRATION_TIME, data, RATES_PROVIDER);
    BlackIborCapletFloorletExpiryStrikeVolatilities resVol =
        (BlackIborCapletFloorletExpiryStrikeVolatilities) res.getVolatilities();
    Surface resSurface = resVol.getSurface();
    int nParams = resSurface.getParameterCount();
    for (int i = 0; i < nParams; ++i) {
      assertEquals(resSurface.getParameter(i), 0.5, 1.0e-11);
    }
  }

  public void recovery_test_normalFlat() {
    double lambdaT = 0.01;
    double lambdaK = 0.01;
    double error = 1.0e-3;
    DirectIborCapletFloorletVolatilityDefinition definition = DirectIborCapletFloorletVolatilityDefinition.of(
        IborCapletFloorletVolatilitiesName.of("test"), USD_LIBOR_3M, ACT_ACT_ISDA, lambdaT, lambdaK,
        GridSurfaceInterpolator.of(CurveInterpolators.LINEAR, CurveInterpolators.LINEAR));
    ImmutableList<Period> maturities = createBlackMaturities();
    DoubleArray strikes = createBlackStrikes();
    RawOptionData data = RawOptionData.of(
        maturities, strikes, ValueType.STRIKE, createFullFlatBlackDataMatrix(),
        DoubleMatrix.filled(maturities.size(), strikes.size(), error), ValueType.NORMAL_VOLATILITY);
    IborCapletFloorletVolatilityCalibrationResult res = CALIBRATOR.calibrate(definition, CALIBRATION_TIME, data, RATES_PROVIDER);
    NormalIborCapletFloorletExpiryStrikeVolatilities resVol =
        (NormalIborCapletFloorletExpiryStrikeVolatilities) res.getVolatilities();
    Surface resSurface = resVol.getSurface();
    int nParams = resSurface.getParameterCount();
    for (int i = 0; i < nParams; ++i) {
      assertEquals(resSurface.getParameter(i), 0.5, 1.0e-12);
    }
  }

  public void recovery_test_normal() {

    double lambdaT = 0.07;
    double lambdaK = 0.07;
    double error = 1.0e-5;
    DirectIborCapletFloorletVolatilityDefinition definition = DirectIborCapletFloorletVolatilityDefinition.of(
        IborCapletFloorletVolatilitiesName.of("test"), USD_LIBOR_3M, ACT_ACT_ISDA, lambdaT, lambdaK,
        GridSurfaceInterpolator.of(CurveInterpolators.LINEAR, CurveInterpolators.LINEAR));
    ImmutableList<Period> maturities = createNormalMaturities();
    DoubleArray strikes = createNormalStrikes();
    RawOptionData data = RawOptionData.of(
        maturities, strikes, ValueType.STRIKE, createFullNormalDataMatrix(),
        DoubleMatrix.filled(maturities.size(), strikes.size(), error), ValueType.NORMAL_VOLATILITY);
    IborCapletFloorletVolatilityCalibrationResult res = CALIBRATOR.calibrate(definition, CALIBRATION_TIME, data, RATES_PROVIDER);
    NormalIborCapletFloorletExpiryStrikeVolatilities resVol =
        (NormalIborCapletFloorletExpiryStrikeVolatilities) res.getVolatilities();
    for (int i = 0; i < strikes.size(); ++i) {
      Pair<List<ResolvedIborCapFloorLeg>, List<Double>> capsAndVols = getCapsNormalVols(i);
      List<ResolvedIborCapFloorLeg> caps = capsAndVols.getFirst();
      List<Double> vols = capsAndVols.getSecond();
      int nCaps = caps.size();
      for (int j = 0; j < nCaps; ++j) {
        ConstantSurface volSurface = ConstantSurface.of(
            Surfaces.normalVolatilityByExpiryStrike("test", ACT_ACT_ISDA), vols.get(j));
        NormalIborCapletFloorletExpiryStrikeVolatilities constVol = NormalIborCapletFloorletExpiryStrikeVolatilities.of(
            USD_LIBOR_3M, CALIBRATION_TIME, volSurface);
        double priceOrg = LEG_PRICER_NORMAL.presentValue(caps.get(j), RATES_PROVIDER, constVol).getAmount();
        double priceCalib = LEG_PRICER_NORMAL.presentValue(caps.get(j), RATES_PROVIDER, resVol).getAmount();
        assertEquals(priceOrg, priceCalib, Math.max(priceOrg, 1d) * TOL);
      }
    }

//    print(res, strikes, 20d);

//    assertEquals(res.getChiSquare(), 0d);
//    assertEquals(res.getChiSquare(), 0d);
//    assertEquals(resVol.getIndex(), USD_LIBOR_3M);
//    assertEquals(resVol.getName(), definition.getName());
//    assertEquals(resVol.getValuationDateTime(), CALIBRATION_TIME);
//    InterpolatedNodalSurface surface = (InterpolatedNodalSurface) resVol.getSurface();
//    for (int i = 0; i < surface.getParameterCount(); ++i) {
//      GenericVolatilitySurfacePeriodParameterMetadata metadata =
//          (GenericVolatilitySurfacePeriodParameterMetadata) surface.getParameterMetadata(i);
//      assertEquals(metadata.getStrike().getValue(), surface.getYValues().get(i));
//    }
  }

  public void recovery_test_normalToBlack() {

    double lambdaT = 0.07;
    double lambdaK = 0.07;
    double error = 1.0e-5;
    DirectIborCapletFloorletVolatilityDefinition definition = DirectIborCapletFloorletVolatilityDefinition.of(
        IborCapletFloorletVolatilitiesName.of("test"), USD_LIBOR_3M, ACT_ACT_ISDA, lambdaT, lambdaK,
        GridSurfaceInterpolator.of(CurveInterpolators.LINEAR, CurveInterpolators.LINEAR), 0.02);
    ImmutableList<Period> maturities = createNormalEquivMaturities();
    DoubleArray strikes = createNormalEquivStrikes();
    RawOptionData data = RawOptionData.of(
        maturities, strikes, ValueType.STRIKE, createFullNormalEquivDataMatrix(),
        DoubleMatrix.filled(maturities.size(), strikes.size(), error), ValueType.NORMAL_VOLATILITY);
    IborCapletFloorletVolatilityCalibrationResult res = CALIBRATOR.calibrate(definition, CALIBRATION_TIME, data, RATES_PROVIDER);
    ShiftedBlackIborCapletFloorletExpiryStrikeVolatilities resVol =
        (ShiftedBlackIborCapletFloorletExpiryStrikeVolatilities) res.getVolatilities();
    for (int i = 0; i < strikes.size(); ++i) {
      Pair<List<ResolvedIborCapFloorLeg>, List<Double>> capsAndVols = getCapsNormalEquivVols(i);
      List<ResolvedIborCapFloorLeg> caps = capsAndVols.getFirst();
      List<Double> vols = capsAndVols.getSecond();
      int nCaps = caps.size();
      for (int j = 0; j < nCaps; ++j) {
        ConstantSurface volSurface = ConstantSurface.of(
            Surfaces.normalVolatilityByExpiryStrike("test", ACT_ACT_ISDA), vols.get(j));
        NormalIborCapletFloorletExpiryStrikeVolatilities constVol = NormalIborCapletFloorletExpiryStrikeVolatilities.of(
            USD_LIBOR_3M, CALIBRATION_TIME, volSurface);
        double priceOrg = LEG_PRICER_NORMAL.presentValue(caps.get(j), RATES_PROVIDER, constVol).getAmount();
        double priceCalib = LEG_PRICER_BLACK.presentValue(caps.get(j), RATES_PROVIDER, resVol).getAmount();
        assertEquals(priceOrg, priceCalib, Math.max(priceOrg, 1d) * TOL);
      }
    }

//    assertEquals(res.getChiSquare(), 0d);
//    assertEquals(res.getChiSquare(), 0d);
//    assertEquals(resVol.getIndex(), USD_LIBOR_3M);
//    assertEquals(resVol.getName(), definition.getName());
//    assertEquals(resVol.getValuationDateTime(), CALIBRATION_TIME);
//    InterpolatedNodalSurface surface = (InterpolatedNodalSurface) resVol.getSurface();
//    for (int i = 0; i < surface.getParameterCount(); ++i) {
//      GenericVolatilitySurfacePeriodParameterMetadata metadata =
//          (GenericVolatilitySurfacePeriodParameterMetadata) surface.getParameterMetadata(i);
//      assertEquals(metadata.getStrike().getValue(), surface.getYValues().get(i));
//    }
  }

}
