package noise.road.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.apache.commons.io.FileUtils;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import noise.road.entity.DbfData;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.ResultsRepository;
import noise.road.repository.ShapeGeometryRepository;
import noise.road.service.fileSaveHelper.FileSaveLogic;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Service
@Slf4j
public class FileSaveService {
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	@Autowired
	private ShapeGeometryRepository shapeGeometryRepsotiory;
	
	@Autowired
	private ResultsRepository resultsRepository;

	public File saveShpFile(int activeFileId, String fileName, List<String> columnNames) throws DataAccessException, FactoryException, IOException, ParseException, IllegalArgumentException {
		long startTime = System.nanoTime();
		List<DbfData> dbfDataList = dbfDataRepository.findByFileId(activeFileId);
		List<ShapeGeometry> shpGeometry = shapeGeometryRepsotiory.findByFileId(activeFileId);
		List<Results> resultsList = resultsRepository.findByFileId(activeFileId);

		SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
		typeBuilder.setName("Shapefile");
		// Setting the Coordinate Reference System (CRS)
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(FileSaveLogic.stringWKT_EOV());
		} catch (FactoryException e) {
			throw new FactoryException("Error during CRS parsing" + e);
		}
        typeBuilder.setCRS(crs);
        typeBuilder.add("geometry", MultiLineString.class); 
		
        for (String columnName : columnNames) {
        	columnName = FileSaveLogic.truncateColumnNames(columnName);
        	Class<?> columnType = FileSaveLogic.determineColumnType(columnName);
        	typeBuilder.add(columnName, columnType);
        }
        
        SimpleFeatureType featureType = typeBuilder.buildFeatureType();
        
        ShapefileDataStore dataStore = null;
	     // Create the temporary directory
	     Path tempDir = null;
	     File tempFolder = null;
	     try {
	         tempDir = Files.createTempDirectory("shapefileSAVETempDir");
	         tempFolder = tempDir.toFile();
	       //  File tempFolder = tempDir.toFile();
	         File shpFile = new File(tempDir.toFile(), fileName + ".shp");
	
	         // Create the shapefile data store
	         ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
	         dataStore = (ShapefileDataStore) dataStoreFactory.createDataStore(shpFile.toURI().toURL());
	
	         // Set the charset for shapefile attribute encoding
	         Charset charset = Charset.forName("UTF-8");
	         dataStore.setCharset(charset);	
	     } catch (IOException e) {
	         throw new IOException("Error during creating temp folder" + e);
	     }
	     
	     // Create the feature collection
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        // Create a WKT reader
        WKTReader wktReader = new WKTReader();
        
        for (int i = 0; i < dbfDataList.size(); i++) {
        	DbfData dbfData = dbfDataList.get(i);
            Results results = resultsList.get(i);
            
            ShapeGeometry shapeGeometry = shpGeometry.get(i);
            String geometryWKT = shapeGeometry.getGeometryWKT();
          
            try {
                // Parse the WKT string to a Geometry object
                Geometry geometry = wktReader.read(geometryWKT);
            	Geometry densifiedGeometry = FileSaveLogic.densifyGeometry(geometry, 2.0);
            	featureBuilder.add(densifiedGeometry);
            	
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getIdentifier());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getSpeed1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getSpeed2());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getSpeed3());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDay1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDay2());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDay3());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNight1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNight2());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNight3());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getIdentifierR());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getSpeed1R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getSpeed2R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getSpeed3R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDay1R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDay2R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDay3R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNight1R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNight2R());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNight3R());
            	
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getLaeqDay());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getLaeqNight());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getLwDay());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getLwNight());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getProtectiveDistanceDay());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getProtectiveDistanceNight());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getImpactAreaDay());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getImpactAreaNight());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getNoiseAtGivenDistanceDay());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getNoiseAtGivenDistanceNight());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getDifferenceDay0());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getDifferenceNight0());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getDifferenceDay1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getDifferenceNight1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getDifferenceDay2());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, results.getDifferenceNight2());

            	SimpleFeature feature = featureBuilder.buildFeature(null);
                featureCollection.add(feature);
            } catch (ParseException e) {
                throw new ParseException("Error during parsing WKT" + e);
            }

        }
        
        // Write the features to the shapefile
        String typeName = "";
        try {
			dataStore.createSchema(featureType);
			typeName = dataStore.getTypeNames()[0];
		} catch (IOException e) {
			throw new IOException("Error during creating dataStore" + e);
		}

        Transaction transaction = new DefaultTransaction();
        
        try (
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                    dataStore.getFeatureWriterAppend(typeName, transaction);
            FeatureIterator<SimpleFeature> featureIterator = 
                    featureCollection.features()) {

           while (featureIterator.hasNext()) {
               SimpleFeature feature = featureIterator.next();
               SimpleFeature newFeature = writer.next();
               newFeature.setAttributes(feature.getAttributes());
               writer.write();
           }

           writer.close();
           featureIterator.close();
           transaction.commit();
           } catch (IOException e) {
        	   throw new IOException("Error during writing file" + e);
			} finally {
               try {
					transaction.close();
				} catch (IOException e) {
					throw new IOException("Error during closing transaction" + e);
				}
           }
        
           dataStore.dispose();
        
           // Logic to zip the shapefile folder
           File generatedZipFile = null;
           try {
        	   Path zipFilePath = Files.createTempFile(fileName, ".zip");

               try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                    ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                   // Zip the contents of the shapefile directory
                   File[] filesToZip = tempDir.toFile().listFiles();
                   if (filesToZip != null) {
                       for (File file : filesToZip) {
                           FileSaveLogic.addToZipFile(file, file.getName(), zipOut);
                       }
                   }

                   generatedZipFile = zipFilePath.toFile();
               }
           } catch (IOException e) {
        	   throw new IOException("Error during creating .zip file" + e);
           }
           
           try {
			FileUtils.deleteDirectory(tempFolder);
			} catch (IOException e) {
				throw new IOException("Error during deleting temp folder" + e);
			} 
           
           long endTime = System.nanoTime();
           long durationInNano = endTime - startTime;
           double durationInSeconds = durationInNano / 1_000_000_000.0;
           log.info("Save File duration in sec: {}", durationInSeconds);
           return generatedZipFile;
	}
	
	public File saveProtectiveAndImpactAreaDistance(int activeFileId, String fileName, String saveName, String saveFolderName) throws DataAccessException, FactoryException, IOException, ParseException, IllegalArgumentException {
		long startTime = System.nanoTime();
		List<ShapeGeometry> shpGeometry = shapeGeometryRepsotiory.findByFileId(activeFileId);
		List<Results> resultsList = resultsRepository.findByFileId(activeFileId);
		
		List<Geometry> newGeometries = FileSaveLogic.createNewGeometry(shpGeometry, resultsList, saveName);
		
		
		// Create the feature type
		SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
		typeBuilder.setName("Shapefile");
		// Setting the Coordinate Reference System (CRS)
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(FileSaveLogic.stringWKT_EOV());
		} catch (FactoryException e) {
			throw new FactoryException("Error during CRS parsing" + e);
		}
		 typeBuilder.setCRS(crs);
		 typeBuilder.add("geometry", MultiLineString.class); // Geometry attribute
		 typeBuilder.add(saveName, String.class);
		 
		 SimpleFeatureType featureType = typeBuilder.buildFeatureType();
		 
		 ShapefileDataStore dataStore = null;
	     // Create the temporary directory
	     Path tempDir = null;
	     File tempFolder = null;
	     try {
	         tempDir = Files.createTempDirectory("shapefileSave" + saveFolderName + "TempDir");
	         tempFolder = tempDir.toFile();
	
	         File shpFile = new File(tempDir.toFile(), fileName + "_" + saveName + ".shp");
	
	         // Create the shapefile data store
	         ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
	         dataStore = (ShapefileDataStore) dataStoreFactory.createDataStore(shpFile.toURI().toURL());
	
	         // Set the charset for shapefile attribute encoding
	         Charset charset = Charset.forName("UTF-8");
	         dataStore.setCharset(charset);	
	     } catch (IOException e) {
	    	 throw new IOException("Error during creating temp folder" + e);
	     }
	     
	     // Create the feature collection
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        
        for (int i = 0; i < newGeometries.size(); i++) {
            Geometry geometry = newGeometries.get(i);
            String attributeValue = i == 0 ? "nappal" : "ejjel"; // Set the attribute value based on the index

            featureBuilder.add(geometry);
            featureBuilder.add(attributeValue); // Add the attribute value
            SimpleFeature feature = featureBuilder.buildFeature(null);
            featureCollection.add(feature);
        }
        
        // Write the features to the shapefile
        String typeName = "";
        try {
			dataStore.createSchema(featureType);
		} catch (IOException e) {
			throw new IOException("Error during creating dataStore" + e);
		}
        
		try {
			typeName = dataStore.getTypeNames()[0];
		} catch (IOException e) {
			throw new IOException("Error during creating dataStore" + e);
		}
        Transaction transaction = new DefaultTransaction();
        try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriterAppend(typeName, transaction);
             FeatureIterator<SimpleFeature> featureIterator = featureCollection.features()) {
            while (featureIterator.hasNext()) {
                SimpleFeature feature = featureIterator.next();
                SimpleFeature newFeature = writer.next();
                newFeature.setAttributes(feature.getAttributes());
                writer.write();
            }

            featureIterator.close();
            writer.close();
            transaction.commit();
        } catch (IOException e) {
        	throw new IOException("Error during writing file" + e);
		} finally {
            try {
				transaction.close();
			} catch (IOException e) {
				throw new IOException("Error during closing transaction" + e);
			}
        }
        dataStore.dispose();
        
        // Logic to zip the shapefile folder
        File generatedZipFile = null;
        try {
     	   Path zipFilePath = Files.createTempFile(fileName + "_" + saveName, ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                // Zip the contents of the shapefile directory
                File[] filesToZip = tempDir.toFile().listFiles();
                if (filesToZip != null) {
                    for (File file : filesToZip) {
                        FileSaveLogic.addToZipFile(file, file.getName(), zipOut);
                    }
                }

                generatedZipFile = zipFilePath.toFile();
            }
        } catch (IOException e) {
        	throw new IOException("Error during creating .zip file" + e);
        }
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        double durationInSeconds = durationInNano / 1_000_000_000.0;
        log.info("Save Protective/Impact duration in sec: {}", durationInSeconds);
        
        try {
			FileUtils.deleteDirectory(tempFolder);
		} catch (IOException e) {
			throw new IOException("Error during deleting temp folder" + e);
		} 
        
        return generatedZipFile;
	}
	
	public File saveToExcel(int activeFileId, String fileName, List<String> columnNames) throws DataAccessException, IOException, IllegalArgumentException {
		
		List<DbfData> dbfDataList = dbfDataRepository.findByFileId(activeFileId);
        List<Results> resultsList = resultsRepository.findByFileId(activeFileId);
        Object[] columns = columnNames.toArray();
        
        TreeMap<Integer, Object[]> excelDataMap = new TreeMap<>();
        
        int dataSize = dbfDataList.size() == 0 ? resultsList.size() : dbfDataList.size();
        int cellCountInARow = columnNames.size();
        
        // header row
        excelDataMap.put(1, columns);
        
        for (int i = 0; i < dataSize; i++) {
        	DbfData dbfData = dbfDataList.get(i);
            Results results = resultsList.get(i);
            
            checkAndAddToMap(i + 2, dbfData, results, excelDataMap, cellCountInARow);
        }
        
        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);
        
        // iterate over the map
        Set<Integer> keyset = excelDataMap.keySet();
        int rowNum = 0;
        
       for (int key : keyset) {
    	   Row row = sheet.createRow(rowNum++);
    	   
    	   Object[] objArr = excelDataMap.get(key);
    	   
    	   int cellNum = 0;
    	   
    	   for (Object obj : objArr) {
    		   Cell cell = row.createCell(cellNum++);
    		   
    		   if (obj instanceof String) {
    			   cell.setCellValue((String) obj);
    		   } else if (obj instanceof Integer) {
    			   cell.setCellValue((Integer) obj);
    		   } else if (obj instanceof Double) {
    			   cell.setCellValue((Double) obj);
    		   }
    	   }
       }

        // Save workbook to a file
        File excelFile = null;
        try {
            excelFile = File.createTempFile(fileName, ".xlsx");
            try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            throw new IOException("Error during creating Excel file" + e);
        } finally {
            workbook.close();
        }
        
        return excelFile;    
	}
	
	private void checkAndAddToMap(int rowIndex, DbfData dbfData, Results results, TreeMap<Integer, Object[]> excelDataMap, int cellCountInARow) {
		Object[] rowValues = new Object[cellCountInARow];
		List<Object> values = new ArrayList<>();
		
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getIdentifier());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getSpeed1());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getSpeed2());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getSpeed3());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatDay1());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatDay2());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatDay3());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatNight1());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatNight2());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatNight3());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getIdentifierR());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getSpeed1R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getSpeed2R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getSpeed3R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatDay1R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatDay2R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatDay3R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatNight1R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatNight2R());
		FileSaveLogic.addNonNullValueToListExcel(values, dbfData.getAcousticCatNight3R());
		
		FileSaveLogic.addNonNullValueToListExcel(values, results.getLaeqDay());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getLaeqNight());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getLwDay());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getLwNight());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getProtectiveDistanceDay());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getProtectiveDistanceNight());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getImpactAreaDay());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getImpactAreaNight());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getNoiseAtGivenDistanceDay());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getNoiseAtGivenDistanceNight());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getDifferenceDay0());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getDifferenceNight0());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getDifferenceDay1());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getDifferenceNight1());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getDifferenceDay2());
		FileSaveLogic.addNonNullValueToListExcel(values, results.getDifferenceNight2());
		
		rowValues = values.toArray();
		excelDataMap.put(rowIndex, rowValues);
	    	 
	}

	
}
