 const columnMapping = {
		    'Azonosító': 'identifier',
		    'I_km/h': 'kmh1',
			'I_ak_kat_N' : 'acCatDay1',
			'II_ak_kat_N': 'acCatDay2',
			'III_ak_kat_N': 'acCatDay3',
			'I_ak_kat_E': 'acCatNight1',
			'II_ak_kat_E': 'acCatNight2',
			'III_ak_kat_E': 'acCatNight3',
			'R_Azonosító': 'reverseIdentifier',
		    'R_I_km/h': 'reverseKmh1',
			'R_I_ak_kat_N': 'reverseAcCatDay1',
			'R_II_ak_kat_N': 'reverseAcCatDay2',
			'R_III_ak_kat_N': 'reverseAcCatDay3',
			'R_I_ak_kat_E': 'reverseAcCatNight1',
			'R_II_ak_kat_E': 'reverseAcCatNight2',
			'R_III_ak_kat_E': 'reverseAcCatNight3'
		};

		const FileUploadComponent = () => {
		    const [file, setFile] = React.useState(null);
		    const [fileName, setFileName] = React.useState('');
		    const [attributeData, setAttributeData] = React.useState([]);
		    const [columnNames, setColumnNames] = React.useState([]);
		
		    const handleFileChange = (event) => {
		        const selectedFile = event.target.files[0];
		        const fileName = selectedFile.name;
		        setFile(selectedFile);
		        setFileName(fileName); 
		    };
		
		    const filterTableBySelectedColumns = () => {
		        const filteredData = attributeData.map((row) => {
		            const newRow = {};
		            
		       let selectedColumnIndex = 0;
		
		        Object.keys(row).forEach((key, index) => {
		            const selectedColumn = columnNames[selectedColumnIndex];
		            if (selectedColumn && selectedColumn !== 'Select Column Name') {
		                newRow[selectedColumn] = row[key];
		            }
		            selectedColumnIndex++;
		            });
		            console.log('NewRow:', newRow);
		            return newRow;
		        });
		        return filteredData;
		    };
		
		    const handleFileUpload = async () => {
		        const formData = new FormData();
		        formData.append('zipFile', file);
		
		        try {
					console.log('FileLoad Request Data:', formData);
		            const response = await fetch('/console/fileLoad', {
		                method: 'POST',
		                body: formData,
		            });
		
		            if (response.ok) {
		                const data = await response.json();
		                console.log('FileLoad Response Data:', data);
		                setAttributeData(data);
		                setColumnNames(Array(data[0] ? Object.keys(data[0]) : []).fill(''));
		            } else {
		                console.error('Failed to upload file');
		            }
		        } catch (error) {
		            console.error('Error uploading file:', error);
		        }
		    };
		
		    const handleSelectChange = (index) => (event) => {
		        const updatedColumnNames = [...columnNames];
		        updatedColumnNames[index] = event.target.value;
		        setColumnNames(updatedColumnNames);
		    };
		
		    const handleFilterClick = async () => {
		        const filteredData = filterTableBySelectedColumns();
		        
		        const mappedData = filteredData.map((row) => {
		        const mappedRow = {};
		        Object.keys(row).forEach((key) => {
		            const mappedKey = columnMapping[key] || key; // Use mapping if available, else use the same key
		            mappedRow[mappedKey] = row[key];
		        });
		        return mappedRow;
		    });
		
		        try {
					console.log('SaveToDatabase Request Data:', { fileName, mappedData });
		            const response = await fetch('/console/saveToDatabase', {
		                method: 'POST',
		                headers: {
		                    'Content-Type': 'application/json',
		                },
		                body: JSON.stringify({ fileName, mappedData }),
		            });
		
		            if (response.ok) {
						console.log('SaveToDatabase Response:', await response.text());
		                console.log('Data filtered and sent to backend successfully');
		                window.location.href = '/console/display';
		                // Optionally handle success here
		            } else {
		                console.error('Failed to filter data');
		            }
		        } catch (error) {
		            console.error('Error filtering data:', error);
		        }
		    };
		
		    return (
		        <div>
		            <h1>KONZOL OLDAL</h1>
		            <input type="file" accept=".zip" onChange={handleFileChange} />
		            <button onClick={handleFileUpload}>Upload</button>
		
		            <table>
		                <thead>
		                    <tr>
		                        {attributeData.length > 0 &&
		                            Object.keys(attributeData[0]).map((key, index) => (
		                                <th key={key}>
		                                    <div>
		                                        <span>{key}</span>
		                                        <select value={columnNames[index]} onChange={handleSelectChange(index)}>
		                                            <option value="">Select Column Name</option>
		                                            {Object.entries(columnMapping).map(([displayName, backendName], idx) => (
						                                <option key={idx} value={backendName}>
						                                    {displayName}
		                                                </option>
		                                            ))}
		                                        </select>
		                                    </div>
		                                </th>
		                            ))}
		                    </tr>
		                </thead>
				                <tbody>
				                    {attributeData.map((row, rowIndex) => (
				                        <tr key={rowIndex}>
				                            {Object.values(row).map((value, columnIndex) => (
				                                <td key={columnIndex}>{value}</td>
				                            ))}
				                        </tr>
				                    ))}
				                </tbody>
		
		            </table>
		
		            <button onClick={handleFilterClick}>Filter</button>
		        </div>
		    );
		};
		
		ReactDOM.render(<FileUploadComponent />, document.getElementById('root'));