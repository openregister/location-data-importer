package uk.gov.gds.location.importer.model

import org.specs2.mutable.Specification

import uk.gov.gds.location.importer.io.FileUtilities.parseCsvLine
import org.joda.time.DateTime
import uk.gov.gds.location.importer.model.CodeLists.BlpuStateCode._
import uk.gov.gds.location.importer.model.CodeLists.LogicalStatusCode._
import uk.gov.gds.location.importer.model.CodeLists.StreetClassificationCode._
import uk.gov.gds.location.importer.model.CodeLists.StreetRecordTypeCode._
import uk.gov.gds.location.importer.model.CodeLists.StreetStateCode._
import uk.gov.gds.location.importer.model.CodeLists.StreetSurfaceCode._

class ModelTests extends Specification {

  "Code Point" should {
    "be able to identify a valid CSV line" in {
      val validLine = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,770874,"S92000003","","S08000013","","S12000041","S13002509","S""""
      CodePoint.isValidCsvLine(parseCsvLine(validLine)) must beTrue
    }

    "be able to extract a code point from a valid CSV line" in {
      val validLine = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,770874,"S92000003","S08000014","S08000013","S12000047","S12000041","S13002509","S""""
      CodePoint.fromCsvLine(parseCsvLine(validLine)).postcode must beEqualTo("dd97ux")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).country must beEqualTo("Scotland")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).gssCode must beEqualTo("S12000041")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).name must beEqualTo("Angus")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).nhsRegionalHealthAuthority.get must beEqualTo("S08000014")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).nhsHealthAuthority.get must beEqualTo("S08000013")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).county.get must beEqualTo("S12000047")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).ward.get must beEqualTo("S13002509")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).easting must beEqualTo(359978)
      CodePoint.fromCsvLine(parseCsvLine(validLine)).northing must beEqualTo(770874)
      CodePoint.fromCsvLine(parseCsvLine(validLine)).lat must beEqualTo(56.827614081674085)
      CodePoint.fromCsvLine(parseCsvLine(validLine)).long must beEqualTo(-2.6573977431257347)
    }

    "be able to extract a code point from a valid CSV line with minimum valid fields" in {
      val validLine = """"DD9 7UX",,"",,,,,,,,359978,770874,"S92000003","","","","S12000041","","S""""
      CodePoint.fromCsvLine(parseCsvLine(validLine)).postcode must beEqualTo("dd97ux")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).country must beEqualTo("Scotland")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).gssCode must beEqualTo("S12000041")
      CodePoint.fromCsvLine(parseCsvLine(validLine)).nhsRegionalHealthAuthority must beNone
      CodePoint.fromCsvLine(parseCsvLine(validLine)).nhsHealthAuthority must beNone
      CodePoint.fromCsvLine(parseCsvLine(validLine)).county must beNone
      CodePoint.fromCsvLine(parseCsvLine(validLine)).ward must beNone
      CodePoint.fromCsvLine(parseCsvLine(validLine)).easting must beEqualTo(359978)
      CodePoint.fromCsvLine(parseCsvLine(validLine)).northing must beEqualTo(770874)
      CodePoint.fromCsvLine(parseCsvLine(validLine)).lat must beEqualTo(56.827614081674085)
      CodePoint.fromCsvLine(parseCsvLine(validLine)).long must beEqualTo(-2.6573977431257347)
    }

    "be able to identify an invalid line with wrong number of columns" in {
      val invalidLine = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,"S92000003","","S08000013","","S12000041","S13002509","S""""
      CodePoint.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to identify an invalid line with unmatched LA" in {
      val invalidLine = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,"S92000003","","S08000013","","donesnt exist","S13002509","S""""
      CodePoint.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to identify an invalid line with unmatched country" in {
      val invalidLine = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,"where?","","S08000013","","S12000041","S13002509","S""""
      CodePoint.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to identify an invalid line with missing data" in {

      val invalidLineNoPostcode = """"",10,"N",9,9,9,0,0,9,0,359978,770874,"S92000003","","S08000013","S12000047","S12000041","S13002509","S""""
      val invalidLineNoCountry = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,770874,"","","S08000013","S12000047","S12000041","S13002509","S""""
      val invalidLineNoDistrict = """"DD9 7UX",10,"N",9,9,9,0,0,9,0,359978,770874,"S92000003","","S08000013","S12000047","","S13002509","S""""

      CodePoint.isValidCsvLine(parseCsvLine(invalidLineNoPostcode)) must beFalse
      CodePoint.isValidCsvLine(parseCsvLine(invalidLineNoCountry)) must beFalse
      CodePoint.isValidCsvLine(parseCsvLine(invalidLineNoDistrict)) must beFalse
    }
  }

  "BLPU" should {

    "be able to identify a valid line" in {
      val validLine = """21,"I",94755,9059007610,1,2,2005-04-05,9059007610,346782.00,732382.00,1,9059,2005-04-05,2010-04-05,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      BLPU.isValidCsvLine(parseCsvLine(validLine)) must beTrue
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """22,"I",94755,9059007610,1,2,2005-04-05,9059007610,346782.00,732382.00,1,9059,2005-04-05,2010-04-05,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      BLPU.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """21,94755,9059007610,1,2,2005-04-05,9059007610,346782.00,732382.00,1,9059,2005-04-05,2010-04-05,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      BLPU.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory column" in {
      val invalidLine = """21,"I",94755,,1,2,2005-04-05,9059007610,346782.00,732382.00,1,9059,2005-04-05,2010-04-05,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      BLPU.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to be constructed from a fully populated csv line" in {
      val line = """21,"I",94755,9059007610,1,2,2005-04-05,9059007610,346782.00,732382.00,1,9059,2005-04-05,2010-04-05,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      blpu(line).uprn must beEqualTo("9059007610")
      blpu(line).blpuState.get must beEqualTo(inUse)
      blpu(line).logicalState.get must beEqualTo(approved)
      blpu(line).startDate must beEqualTo(new DateTime(2005, 4, 5, 0, 0))
      blpu(line).lastUpdated must beEqualTo(new DateTime(2009, 5, 22, 0, 0))
      blpu(line).endDate.get must beEqualTo(new DateTime(2010, 4, 5, 0, 0))
      blpu(line).lat must beEqualTo(56.48052636343243)
      blpu(line).long must beEqualTo(-2.8656333885406062)
      blpu(line).receivesPost must beEqualTo("S")
      blpu(line).canReceivePost must beEqualTo(true)
      blpu(line).postcode must beEqualTo("DD5 3BZ")
    }

    "be able to be constructed from a csv line with only mandatory fields" in {
      val line = """21,"I",94755,9059007610,1,,,,346782.00,732382.00,1,9059,2005-04-05,,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      blpu(line).uprn must beEqualTo("9059007610")
      blpu(line).blpuState must beEqualTo(None)
      blpu(line).logicalState.get must beEqualTo(approved)
      blpu(line).startDate must beEqualTo(new DateTime(2005, 4, 5, 0, 0))
      blpu(line).lastUpdated must beEqualTo(new DateTime(2009, 5, 22, 0, 0))
      blpu(line).endDate must beEqualTo(None)
      blpu(line).lat must beEqualTo(56.48052636343243)
      blpu(line).long must beEqualTo(-2.8656333885406062)
      blpu(line).receivesPost must beEqualTo("S")
      blpu(line).canReceivePost must beEqualTo(true)
      blpu(line).postcode must beEqualTo("DD5 3BZ")
    }

    "be able to be constructed from a csv line with only mandatory fields - indicating not able to recieve post" in {
      val line = """21,"I",94755,9059007610,1,,,,346782.00,732382.00,1,9059,2005-04-05,,2009-05-22,2005-04-05,"N","DD5 3BZ",0"""
      blpu(line).receivesPost must beEqualTo("N")
      blpu(line).canReceivePost must beEqualTo(false)
    }


    "be able to be made into correct type" in {
      val line = """21,"I",94755,9059007610,1,,,,346782.00,732382.00,1,9059,2005-04-05,,2009-05-22,2005-04-05,"S","DD5 3BZ",0"""
      blpu(line).isInstanceOf[AddressBase] must beTrue
      blpu(line).isInstanceOf[BLPU] must beTrue
    }
  }


  "LPI" should {

    "be able to identify a valid line" in {
      val validLine = """24,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","UNIT 1",,"",,"","WEST PITKERRO",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(validLine)) must beTrue
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """1,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","UNIT 1",,"",,"","WEST PITKERRO",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """24,92423,9059082524,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","UNIT 1",,"",,"","WEST PITKERRO",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory column" in {
      val invalidLine = """24,"I",92423,,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","UNIT 1",,"",,"","WEST PITKERRO",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory language column" in {
      val invalidLine = """24,"I",92423,,"9059L000069680","",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","UNIT 1",,"",,"","WEST PITKERRO",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to identify an invalid line due to missing required PAO information" in {
      val missingBothPaoTextAndPaoStartNumber = """24,"I",1082431,100100077917,"6815L000701604","ENG",1,2001-05-10,,2001-05- 15,2001-05-10,,"",,"","",,"",,"","",5801201,1,"","","""""
      LPI.isValidCsvLine(parseCsvLine(missingBothPaoTextAndPaoStartNumber)) must beFalse

      val missingOnlyPaoText = """24,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","",1,"",,"","",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(missingOnlyPaoText)) must beTrue

      val missingOnlyPaoStartNumber = """24,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","",,"",,"","TEXT",7803241,"1","","","Y""""
      LPI.isValidCsvLine(parseCsvLine(missingOnlyPaoStartNumber)) must beTrue
    }

    "be able to be constructed from a fully populated csv line" in {
      val validLine = """24,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,2006-04-01,2005-04-05,2005-04-05,99,"SAO Start Suffix",100,"SAO End Suffix","Sao Text",1,"PAO Start Suffix",2,"PAO End Suffix","PAO Text",7803241,"1","Area 51","level","Y""""
      lpi(validLine).uprn must beEqualTo("9059082524")
      lpi(validLine).usrn must beEqualTo("7803241")
      lpi(validLine).logicalState.get must beEqualTo(approved)
      lpi(validLine).startDate must beEqualTo(new DateTime(2005, 4, 5, 0, 0))
      lpi(validLine).endDate.get must beEqualTo(new DateTime(2006, 4, 1, 0, 0))
      lpi(validLine).lastUpdated must beEqualTo(new DateTime(2005, 4, 5, 0, 0))
      lpi(validLine).paoStartNumber.get must beEqualTo("1")
      lpi(validLine).paoStartSuffix.get must beEqualTo("PAO Start Suffix")
      lpi(validLine).paoEndNumber.get must beEqualTo("2")
      lpi(validLine).paoEndSuffix.get must beEqualTo("PAO End Suffix")
      lpi(validLine).paoText.get must beEqualTo("PAO Text")
      lpi(validLine).saoStartNumber.get must beEqualTo("99")
      lpi(validLine).saoStartSuffix.get must beEqualTo("SAO Start Suffix")
      lpi(validLine).saoEndNumber.get must beEqualTo("100")
      lpi(validLine).saoEndSuffix.get must beEqualTo("SAO End Suffix")
      lpi(validLine).saoText.get must beEqualTo("Sao Text")
      lpi(validLine).areaName.get must beEqualTo("Area 51")
      lpi(validLine).officialAddress.get must beTrue
      lpi(validLine).language must beEqualTo("ENG")
    }

    "be able to be constructed from a minimum populated csv line" in {
      val validLine = """24,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,,2005-04-05,2005-04-05,,"",,"","",,"",,"","",7803241,"1","","","""""
      lpi(validLine).uprn must beEqualTo("9059082524")
      lpi(validLine).usrn must beEqualTo("7803241")
      lpi(validLine).logicalState.get must beEqualTo(approved)
      lpi(validLine).startDate must beEqualTo(new DateTime(2005, 4, 5, 0, 0))
      lpi(validLine).endDate must beEqualTo(None)
      lpi(validLine).lastUpdated must beEqualTo(new DateTime(2005, 4, 5, 0, 0))
      lpi(validLine).paoStartNumber must beEqualTo(None)
      lpi(validLine).paoStartSuffix must beEqualTo(None)
      lpi(validLine).paoEndNumber must beEqualTo(None)
      lpi(validLine).paoEndSuffix must beEqualTo(None)
      lpi(validLine).paoText must beEqualTo(None)
      lpi(validLine).saoStartNumber must beEqualTo(None)
      lpi(validLine).saoStartSuffix must beEqualTo(None)
      lpi(validLine).saoEndNumber must beEqualTo(None)
      lpi(validLine).saoEndSuffix must beEqualTo(None)
      lpi(validLine).saoText must beEqualTo(None)
      lpi(validLine).areaName must beEqualTo(None)
      lpi(validLine).officialAddress must beEqualTo(None)

    }

    "be able to be made into correct type" in {
      val validLine = """24,"I",92423,9059082524,"9059L000069680","ENG",1,2005-04-05,2006-04-01,2005-04-05,2005-04-05,99,"SAO Start Suffix",100,"SAO End Suffix","Sao Text",1,"PAO Start Suffix",2,"PAO End Suffix","PAO Text",7803241,"1","Area 51","level","Y""""
      lpi(validLine).isInstanceOf[AddressBase] must beTrue
      lpi(validLine).isInstanceOf[LPI] must beTrue
    }
  }


  "Street" should {

    val completeValidLine = """11,"I",1151,709895,2,9053,2,2010-02-05,1,8,0,2008-01-25,2010-01-01,2008-10-09,2008-01-25,347600.00,734728.00,347561.00,734677.00,999"""
    val incompleteValidLine = """11,"I",1151,709895,2,9053,,,,,0,2008-01-25,,2008-10-09,2008-01-25,347600.00,734728.00,347561.00,734677.00,999"""

    "be able to identify a valid line" in {
      Street.isValidCsvLine(parseCsvLine(completeValidLine)) must beTrue
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """17,"I",1151,709895,2,9053,2,2010-02-05,1,8,0,2008-01-25,2010-01-01,2008-10-09,2008-01-25,347600.00,734728.00,347561.00,734677.00,999"""
      Street.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """17,"I",709895,2,9053,,,,,0,2008-01-25,,2008-10-09,2008-01-25,347600.00,734728.00,347561.00,734677.00,999"""
      Street.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }

    "be able to be constructed from a fully populated csv line" in {
      street(completeValidLine).usrn must beEqualTo("709895")
      street(completeValidLine).recordType.get must beEqualTo(streetDescription)
      street(completeValidLine).state.get must beEqualTo(open)
      street(completeValidLine).surface.get must beEqualTo(metalled)
      street(completeValidLine).classification.get must beEqualTo(allVehicles)
      street(completeValidLine).startDate must beEqualTo(new DateTime(2008, 1, 25, 0, 0))
      street(completeValidLine).endDate.get must beEqualTo(new DateTime(2010, 1, 1, 0, 0))
      street(completeValidLine).lastUpdated must beEqualTo(new DateTime(2008, 10, 9, 0, 0))
    }

    "be able to be constructed from a minimum populated csv line" in {
      street(incompleteValidLine).usrn must beEqualTo("709895")
      street(incompleteValidLine).recordType.get must beEqualTo(streetDescription)
      street(incompleteValidLine).state must beEqualTo(None)
      street(incompleteValidLine).surface must beEqualTo(None)
      street(incompleteValidLine).classification must beEqualTo(None)
      street(incompleteValidLine).startDate must beEqualTo(new DateTime(2008, 1, 25, 0, 0))
      street(incompleteValidLine).endDate must beEqualTo(None)
      street(incompleteValidLine).lastUpdated must beEqualTo(new DateTime(2008, 10, 9, 0, 0))

    }

    "be able to be made into correct type" in {
      street(completeValidLine).isInstanceOf[AddressBase] must beTrue
      street(completeValidLine).isInstanceOf[Street] must beTrue
    }
  }


  "Street Descriptor " should {

    val completeValidLine = """15,"I",1142,705576,"ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES","WESTHALL","KELLAS","ANGUS","ENG""""
    val incompleteValidLine = """15,"I",1142,705576,"ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES","","","WESTHALL","ENG""""

    "be able to identify a valid line" in {
      StreetDescriptor.isValidCsvLine(parseCsvLine(completeValidLine)) must beTrue
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """16,"I",1142,705576,"ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES","WESTHALL","KELLAS","ANGUS","ENG""""
      StreetDescriptor.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """16,1,2,1142,705576,"ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES","WESTHALL","KELLAS","ANGUS","ENG""""
      StreetDescriptor.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory columns" in {
      val invalidLine = """15,"I",1142,,"ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES","WESTHALL","KELLAS","ANGUS","ENG""""
      StreetDescriptor.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory language columns" in {
      val invalidLine = """15,"I",1142,,"ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES","WESTHALL","KELLAS","ANGUS","""""
      StreetDescriptor.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to be constructed from a fully populated csv line" in {
      streetDescriptor(completeValidLine).usrn must beEqualTo("705576")
      streetDescriptor(completeValidLine).streetDescription must beEqualTo("ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES")
      streetDescriptor(completeValidLine).localityName.get must beEqualTo("WESTHALL")
      streetDescriptor(completeValidLine).townName.get must beEqualTo("KELLAS")
      streetDescriptor(completeValidLine).administrativeArea must beEqualTo("ANGUS")
      streetDescriptor(completeValidLine).language must beEqualTo("ENG")
    }

    "be able to be constructed from a minimum populated csv line" in {
      streetDescriptor(incompleteValidLine).usrn must beEqualTo("705576")
      streetDescriptor(incompleteValidLine).streetDescription must beEqualTo("ZU315 FROM B978 NORTH OF PITKERRO HOUSE TO ZC4 JUNCTION SOUTH OF WESTHALL FARM COTTAGES")
      streetDescriptor(incompleteValidLine).localityName must beEqualTo(None)
      streetDescriptor(incompleteValidLine).townName must beEqualTo(None)
      streetDescriptor(incompleteValidLine).administrativeArea must beEqualTo("WESTHALL")
      streetDescriptor(completeValidLine).language must beEqualTo("ENG")
    }

    "be able to be made into correct type" in {
      streetDescriptor(completeValidLine).isInstanceOf[AddressBase] must beTrue
      streetDescriptor(completeValidLine).isInstanceOf[StreetDescriptor] must beTrue
    }
  }


  "Organisation " should {

    val completeValidLine = """31,"I",68275,9059056630,"9059O000001011","TMA Accountants","Legal Name",2012-02-01,2013-01-01,2012-02-01,2012-02-01"""
    val incompleteValidLine = """31,"I",68275,9059056630,"9059O000001011","TMA Accountants","",2012-02-01,,2012-02-01,2012-02-01"""

    "be able to identify a valid line" in {
      Organisation.isValidCsvLine(parseCsvLine(completeValidLine)) must beTrue
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """1,"I",68275,9059056630,"9059O000001011","TMA Accountants","Legal Name",2012-02-01,2013-01-01,2012-02-01,2012-02-01"""
      Organisation.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """31,68275,9059056630,"9059O000001011","TMA Accountants","Legal Name",2012-02-01,2013-01-01,2012-02-01,2012-02-01"""
      Organisation.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }


    "be able to identify an invalid line due to missing mandatory columns" in {
      val inValidLine = """31,"I",68275,,"9059O000001011","TMA Accountants","Legal Name",2012-02-01,2013-01-01,2012-02-01,2012-02-01"""
      Organisation.isValidCsvLine(parseCsvLine(inValidLine)) must beFalse
    }

    "be able to be constructed from a fully populated csv line" in {
      organisation(completeValidLine).uprn must beEqualTo("9059056630")
      organisation(completeValidLine).organistation must beEqualTo("TMA Accountants")
      organisation(completeValidLine).startDate must beEqualTo(new DateTime(2012, 2, 1, 0, 0))
      organisation(completeValidLine).endDate.get must beEqualTo(new DateTime(2013, 1, 1, 0, 0))
      organisation(completeValidLine).lastUpdated must beEqualTo(new DateTime(2012, 2, 1, 0, 0))
    }

    "be able to be constructed from a minimum populated csv line" in {
      organisation(incompleteValidLine).uprn must beEqualTo("9059056630")
      organisation(incompleteValidLine).organistation must beEqualTo("TMA Accountants")
      organisation(incompleteValidLine).startDate must beEqualTo(new DateTime(2012, 2, 1, 0, 0))
      organisation(incompleteValidLine).endDate must beEqualTo(None)
      organisation(incompleteValidLine).lastUpdated must beEqualTo(new DateTime(2012, 2, 1, 0, 0))
    }

    "be able to be made into correct type" in {
      organisation(completeValidLine).isInstanceOf[AddressBase] must beTrue
      organisation(completeValidLine).isInstanceOf[Organisation] must beTrue
    }
  }

  "Classification " should {

    val completeValidLine = """32,"I",94712,9059004789,"9059C000080071","RD04","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
    val incompleteValidLine = """32,"I",94712,9059004789,"9059C000080071","RD04","AddressBase Premium Classification Scheme",1.0,2010-04-21,,2011-04-13,2010-04-21"""

    "be able to identify a valid line" in {
      Classification.isValidCsvLine(parseCsvLine(completeValidLine)) must beTrue
    }

    "be able to identify a residential property" in {
      val dwelling = """32,"I",94712,9059004789,"9059C000080071","RD04","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.fromCsvLine(parseCsvLine(dwelling)).isResidential must beTrue
      Classification.fromCsvLine(parseCsvLine(dwelling)).isCommercial must beFalse
      Classification.fromCsvLine(parseCsvLine(dwelling)).isEducational must beFalse

      val multipleOccupancy = """32,"I",94712,9059004789,"9059C000080071","RH04","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.fromCsvLine(parseCsvLine(multipleOccupancy)).isResidential must beTrue
      Classification.fromCsvLine(parseCsvLine(multipleOccupancy)).isCommercial must beFalse
      Classification.fromCsvLine(parseCsvLine(multipleOccupancy)).isEducational must beFalse

      val institute = """32,"I",94712,9059004789,"9059C000080071","RI04","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.fromCsvLine(parseCsvLine(institute)).isResidential must beTrue
      Classification.fromCsvLine(parseCsvLine(institute)).isCommercial must beFalse
      Classification.fromCsvLine(parseCsvLine(institute)).isEducational must beFalse
    }

    "be able to identify a commercial property" in {
      val commercial = """32,"I",94712,9059004789,"9059C000080071","CA1","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.fromCsvLine(parseCsvLine(commercial)).isCommercial must beTrue
      Classification.fromCsvLine(parseCsvLine(commercial)).isResidential must beFalse
      Classification.fromCsvLine(parseCsvLine(commercial)).isEducational must beFalse
    }

    "be able to identify an educational property" in {
      val school = """32,"I",94712,9059004789,"9059C000080071","CE01","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.fromCsvLine(parseCsvLine(school)).isEducational must beTrue
      Classification.fromCsvLine(parseCsvLine(school)).isCommercial must beFalse
      Classification.fromCsvLine(parseCsvLine(school)).isResidential must beFalse
    }

    "be able to identify a non-residential property" in {
      val nonResidential = """32,"I",94712,9059004789,"9059C000080071","M","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.fromCsvLine(parseCsvLine(nonResidential)).isResidential must beFalse
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """1,"I",94712,9059004789,"9059C000080071","RD04","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """32,94712,9059004789,"9059C000080071","RD04","AddressBase Premium Classification Scheme",1.0,2010-04-21,,2011-04-13,2010-04-21"""
      Classification.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory columns" in {
      val invalidLine = """32,"I",94712,,"9059C000080071","RD04","AddressBase Premium Classification Scheme",1.0,2010-04-21,2012-01-01,2011-04-13,2010-04-21"""
      Classification.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to be constructed from a fully populated csv line" in {
      classification(completeValidLine).uprn must beEqualTo("9059004789")
      classification(completeValidLine).classificationCode must beEqualTo("RD04")
      classification(completeValidLine).startDate must beEqualTo(new DateTime(2010, 4, 21, 0, 0))
      classification(completeValidLine).endDate.get must beEqualTo(new DateTime(2012, 1, 1, 0, 0))
      classification(completeValidLine).lastUpdated must beEqualTo(new DateTime(2011, 4, 13, 0, 0))
      classification(completeValidLine).primaryUse must beEqualTo("Residential")
      classification(completeValidLine).secondaryUse must beEqualTo(Some("Dwelling"))
    }

    "be able to be constructed from a minimum populated csv line" in {
      classification(incompleteValidLine).uprn must beEqualTo("9059004789")
      classification(incompleteValidLine).classificationCode must beEqualTo("RD04")
      classification(incompleteValidLine).startDate must beEqualTo(new DateTime(2010, 4, 21, 0, 0))
      classification(incompleteValidLine).endDate must beEqualTo(None)
      classification(incompleteValidLine).lastUpdated must beEqualTo(new DateTime(2011, 4, 13, 0, 0))
      classification(completeValidLine).primaryUse must beEqualTo("Residential")
      classification(completeValidLine).secondaryUse must beEqualTo(Some("Dwelling"))
    }

    "be able to be made into correct type" in {
      classification(completeValidLine).isInstanceOf[AddressBase] must beTrue
      classification(completeValidLine).isInstanceOf[Classification] must beTrue
    }
  }

  "DeliveryPoint" should {

    "be able to identify a valid line" in {
      val validLine = """28,"I",262323,100021769440,,12077423,"","","","",46,"","VINCENT ROAD","","","KINGSTON UPON THAMES","KT1 3HJ","S","","","","","","",2013-09-27,2002-12-06,,2010-09-14,2010-09-14"""
      DeliveryPoint.isValidCsvLine(parseCsvLine(validLine)) must beTrue
    }

    "be able to identify an invalid line due to wrong type" in {
      val wrongRecordIdentifier = """8,"I",262323,100021769440,,12077423,"","","","",46,"","VINCENT ROAD","","","KINGSTON UPON THAMES","KT1 3HJ","S","","","","","","",2013-09-27,2002-12-06,,2010-09-14,2010-09-14"""
      DeliveryPoint.isValidCsvLine(parseCsvLine(wrongRecordIdentifier)) must beFalse
    }

    "be able to identify an invalid line due to wrong number of columns" in {
      val wrongNumberOfColumns = """28,"I",100021769440,,12077423,"","","","",46,"","VINCENT ROAD","","","KINGSTON UPON THAMES","KT1 3HJ","S","","","","","","",2013-09-27,2002-12-06,,2010-09-14,2010-09-14"""
      DeliveryPoint.isValidCsvLine(parseCsvLine(wrongNumberOfColumns)) must beFalse
    }

    "be able to identify an invalid line due to missing mandatory column" in {
      val invalidLine = """28,"I",262323,100021769440,,12077423,"","","","",46,"","VINCENT ROAD","","","KINGSTON UPON THAMES",,"S","","","","","","",2013-09-27,2002-12-06,,2010-09-14,2010-09-14"""
      DeliveryPoint.isValidCsvLine(parseCsvLine(invalidLine)) must beFalse
    }

    "be able to be constructed from a fully populated csv line" in {
      val line = """28,"I",262323,100021769440,,12077423,"","","sub building","building",46,"dependant thoroughfare","VINCENT ROAD","doubleDependantLocality","dependantLocality","KINGSTON UPON THAMES","KT1 3HJ","S","","","","","","",2013-09-27,2002-12-06,2014-01-01,2010-09-14,2010-09-14"""
      deliveryPoint(line).uprn must beEqualTo("100021769440")
      deliveryPoint(line).postcode must beEqualTo("KT1 3HJ")
      deliveryPoint(line).subBuildingName.get must beEqualTo("sub building")
      deliveryPoint(line).buildingName.get must beEqualTo("building")
      deliveryPoint(line).buildingNumber.get must beEqualTo("46")
      deliveryPoint(line).dependantThoroughfareName.get must beEqualTo("dependant thoroughfare")
      deliveryPoint(line).thoroughfareName.get must beEqualTo("VINCENT ROAD")
      deliveryPoint(line).doubleDependantLocality.get must beEqualTo("doubleDependantLocality")
      deliveryPoint(line).dependantLocality.get must beEqualTo("dependantLocality")
      deliveryPoint(line).startDate must beEqualTo(new DateTime(2002, 12, 6, 0, 0))
      deliveryPoint(line).endDate.get must beEqualTo(new DateTime(2014, 1, 1, 0, 0))
      deliveryPoint(line).lastUpdated must beEqualTo(new DateTime(2010, 9, 14, 0, 0))
    }

    "be able to be constructed from a mimum populated csv line" in {
      val line = """28,"I",262323,100021769440,,12077423,"","","","",,"","","","","KINGSTON UPON THAMES","KT1 3HJ","S","","","","","","",2013-09-27,2002-12-06,,2010-09-14,2010-09-14"""
      deliveryPoint(line).uprn must beEqualTo("100021769440")
      deliveryPoint(line).postcode must beEqualTo("KT1 3HJ")
      deliveryPoint(line).subBuildingName must beEqualTo(None)
      deliveryPoint(line).buildingName must beEqualTo(None)
      deliveryPoint(line).buildingNumber must beEqualTo(None)
      deliveryPoint(line).dependantThoroughfareName must beEqualTo(None)
      deliveryPoint(line).thoroughfareName must beEqualTo(None)
      deliveryPoint(line).doubleDependantLocality must beEqualTo(None)
      deliveryPoint(line).dependantLocality must beEqualTo(None)
      deliveryPoint(line).startDate must beEqualTo(new DateTime(2002, 12, 6, 0, 0))
      deliveryPoint(line).endDate.isDefined must beFalse
      deliveryPoint(line).lastUpdated must beEqualTo(new DateTime(2010, 9, 14, 0, 0))
    }

    "be able to be made into correct type" in {
      val line = """28,"I",262323,100021769440,,12077423,"","","","",46,"","VINCENT ROAD","","","KINGSTON UPON THAMES","KT1 3HJ","S","","","","","","",2013-09-27,2002-12-06,,2010-09-14,2010-09-14"""
      deliveryPoint(line).isInstanceOf[AddressBase] must beTrue
      deliveryPoint(line).isInstanceOf[DeliveryPoint] must beTrue
    }
  }

  private def deliveryPoint(line: String) = DeliveryPoint.fromCsvLine(parseCsvLine(line))

  private def blpu(line: String) = BLPU.fromCsvLine(parseCsvLine(line))

  private def lpi(line: String) = LPI.fromCsvLine(parseCsvLine(line))

  private def street(line: String) = Street.fromCsvLine(parseCsvLine(line))

  private def streetDescriptor(line: String) = StreetDescriptor.fromCsvLine(parseCsvLine(line))

  private def organisation(line: String) = Organisation.fromCsvLine(parseCsvLine(line))

  private def classification(line: String) = Classification.fromCsvLine(parseCsvLine(line))
}
