package noise.road.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import noise.road.dto.DbfDataDTO;
import noise.road.entity.DbfData;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.ResultsRepository;
import noise.road.repository.ShapeGeometryRepository;

@Service
public class FileSaveService {
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	@Autowired
	private ShapeGeometryRepository shapeGeometryRepsotiory;
	
	@Autowired
	private ResultsRepository resultsRepository;

	public File saveShpFile(int activeFileId, String fileName, List<String> columnNames) {
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	     try {
	         tempDir = Files.createTempDirectory("shapefileSAVETempDir");
	
	       //  File tempFolder = tempDir.toFile();
	         File shpFile = new File(tempDir.toFile(), fileName + ".shp");
	
	         // Create the shapefile data store
	         ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
	         dataStore = (ShapefileDataStore) dataStoreFactory.createDataStore(shpFile.toURI().toURL());
	
	         // Set the charset for shapefile attribute encoding
	         Charset charset = Charset.forName("UTF-8");
	         dataStore.setCharset(charset);	
	     } catch (IOException e) {
	         e.printStackTrace();
	         // Handle or throw the exception as needed
	     }
	     
	     // Create the feature collection
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        
        for (int i = 0; i < dbfDataList.size(); i++) {
        	DbfData dbfData = dbfDataList.get(i);
            Results results = resultsList.get(i);
            
            ShapeGeometry shapeGeometry = shpGeometry.get(i);
            String geometryWKT = shapeGeometry.getGeometryWKT();

            // Create a WKT reader
            WKTReader wktReader = new WKTReader();
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
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDayR1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDayR2());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatDayR3());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNightR1());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNightR2());
            	FileSaveLogic.addNonNullAttribute(featureBuilder, dbfData.getAcousticCatNightR3());
            	
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
                e.printStackTrace();
                // Handle or throw the exception as needed
            }

        }
        
        // Write the features to the shapefile
        String typeName = "";
        try {
			dataStore.createSchema(featureType);
			typeName = dataStore.getTypeNames()[0];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      //  dataStore.forceSchemaCRS(shapeData.getCoordinateReferenceSystem());
       // dataStore.createSchema(featureType);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			} finally {
               try {
					transaction.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
               e.printStackTrace();
               // Handle or throw the exception as needed
           }

           return generatedZipFile;
	}
	
	public File saveProtectiveDistance(int activeFileId, String fileName) {
		List<ShapeGeometry> shpGeometry = shapeGeometryRepsotiory.findByFileId(activeFileId);
		List<Results> resultsList = resultsRepository.findByFileId(activeFileId);
		
		List<Geometry> newGeometries = FileSaveLogic.createNewGeometry(shpGeometry, resultsList);
		
		
		// Create the feature type
		SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
		typeBuilder.setName("Shapefile");
		// Setting the Coordinate Reference System (CRS)
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(FileSaveLogic.stringWKT_EOV());
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 typeBuilder.setCRS(crs);
		 typeBuilder.add("geometry", MultiLineString.class); // Geometry attribute
		 typeBuilder.add("Vedotav", String.class);
		 
		 SimpleFeatureType featureType = typeBuilder.buildFeatureType();
		 
		 ShapefileDataStore dataStore = null;
	     // Create the temporary directory
	     Path tempDir = null;
	     try {
	         tempDir = Files.createTempDirectory("shapefileSavePROTECTIVETempDir");
	
	       //  File tempFolder = tempDir.toFile();
	         File shpFile = new File(tempDir.toFile(), fileName + "_vedotav" + ".shp");
	
	         // Create the shapefile data store
	         ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
	         dataStore = (ShapefileDataStore) dataStoreFactory.createDataStore(shpFile.toURI().toURL());
	
	         // Set the charset for shapefile attribute encoding
	         Charset charset = Charset.forName("UTF-8");
	         dataStore.setCharset(charset);	
	     } catch (IOException e) {
	         e.printStackTrace();
	         // Handle or throw the exception as needed
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		try {
			typeName = dataStore.getTypeNames()[0];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
				transaction.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        dataStore.dispose();
        
        // Logic to zip the shapefile folder
        File generatedZipFile = null;
        try {
     	   Path zipFilePath = Files.createTempFile(fileName + "_vedotav", ".zip");

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
            e.printStackTrace();
            // Handle or throw the exception as needed
        }

        return generatedZipFile;
	}
	

}
