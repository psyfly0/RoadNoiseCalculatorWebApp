package noise.road.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import noise.road.dto.ShapeDataDTO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

@Service
public class FileReadService {

	private ShapeDataDTO shapeData;
    
	public ShapeDataDTO readShapefileFromZip(MultipartFile zipFile) throws IOException {
        Path tempDir = Files.createTempDirectory("shapefileTempDir");
        File tempFolder = tempDir.toFile();

        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                File entryFile = new File(tempFolder, fileName);

                if (!entry.isDirectory()) {
                    extractEntry(zipInputStream, entryFile);
                }
            }
        }
        
        File shpFile = findShpFile(tempFolder);
        File dbfFile = findDbfFile(tempFolder);

        shapeData = extractShapeData(shpFile, dbfFile);
        
        FileUtils.deleteDirectory(tempFolder); // Delete temp directory and its content

        return shapeData;
    }
	
	private File findShpFile(File tempFolder) {
        File[] files = tempFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".shp")) {
                    return file;
                }
            }
        }
        return null;
    }
	
	private File findDbfFile(File tempFolder) {
        File[] files = tempFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".dbf")) {
                    return file;
                }
            }
        }
        return null;
    }
           
    private void extractEntry(ZipInputStream zipInputStream, File entryFile) throws IOException {
        byte[] buffer = new byte[1024];
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(entryFile))) {
            int len;
            while ((len = zipInputStream.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
        }
    }
    
    
   // Method to extract geometries and schema from a shapefile
    private ShapeDataDTO extractShapeData(File shpFile, File dbfFile) throws IOException {
		ShapeDataDTO shapeData = null;
        ShapefileDataStore dataStore = null;
        try {
        	if (shpFile == null || dbfFile == null) {
                throw new IllegalArgumentException("Required files are missing in the ZIP");
            }
            dataStore = new ShapefileDataStore(shpFile.toURI().toURL());
            String typeName = dataStore.getTypeNames()[0];
            SimpleFeatureType schema;
            List<Geometry> geometries = new ArrayList<>();;
            List<SimpleFeature> featureList = new ArrayList<>();;
            
            // Extracting geometries and schema
            try (SimpleFeatureIterator featureIterator = dataStore.getFeatureSource(typeName).getFeatures().features()) {
                schema = dataStore.getSchema(typeName);
                GeometryDescriptor geometryDescriptor = schema.getGeometryDescriptor();
                SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
                builder.setName(schema.getName());
                builder.setCRS(schema.getCoordinateReferenceSystem());
                builder.setDefaultGeometry(geometryDescriptor.getName().getLocalPart());
                builder.add(geometryDescriptor);
                
                while (featureIterator.hasNext()) {
                    SimpleFeature feature = featureIterator.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    geometries.add(geometry);
                    featureList.add(feature);
                }  
                featureIterator.close();

        	}
            List<Map<String, Object>> attributes = readDbfFile(dbfFile);
            if (!geometries.isEmpty()) {            	
            	shapeData = new ShapeDataDTO(geometries, schema, attributes);
            }
        } catch (IllegalArgumentException e) {
        	throw e;
        }  catch (IOException e) {
            throw new IOException("Error reading .shp file from ZIP" + e);
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
        
        return shapeData;			
    }

    // Method to read attribute data from a DBF file
    public List<Map<String, Object>> readDbfFile(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);
             FileChannel channel = inputStream.getChannel();
             DbaseFileReader dbfReader = new DbaseFileReader(channel, false, Charset.defaultCharset())) {

            // get column names from header	
            DbaseFileHeader header = dbfReader.getHeader();
            List<String> columns = new ArrayList<>();
            for (int i = 0; i < header.getNumFields(); i++) {
                columns.add(header.getFieldName(i));
            }

            List<Map<String, Object>> records = new ArrayList<>();
            while (dbfReader.hasNext()) {
                Object[] record = dbfReader.readEntry();
                Map<String, Object> recordMap = new LinkedHashMap<>();
                
                for (int i = 0; i < record.length; i++) {
                    recordMap.put(columns.get(i), record[i]);
                }
                records.add(recordMap);
            }
            dbfReader.close();
            return records;        	
        } catch (IOException e) {
            throw new IOException("Error reading .dbf file from ZIP" + e);
        }
    }
}
