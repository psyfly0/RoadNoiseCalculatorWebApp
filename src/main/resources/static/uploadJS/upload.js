
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
		
	const parameterMapping = {
		    'lthDay': 'Határérték Nappal',
		    'lthNight': 'Határérték Éjjel',
		    'roadSurfaceRoughness': 'K (útburkolati érdesség)',
		    'reflection': 'Kr (reflexió)',
		    'soundAbsorptionFactor': 'C (hangelnyelési tényező)',
		    'angleOfView': 'Látószög',
		    'trafficType': 'Forgalom típusa',
		    'slopeElevation': 'Lejtés/emelkedés (%-ban)',
		};
	
	const defaultValues = {
	    'lthDay': 65.0,
	    'lthNight': 55.0,
	    'roadSurfaceRoughness': 0.29,
	    'reflection': 0.5,
	    'soundAbsorptionFactor': 12.5,
	    'angleOfView': 180.0,
	    'trafficType': 'egyenletes',
	    'slopeElevation': 0.0,
	};
	
	const selectableValues = {
	    'lthDay': [50.0, 55.0, 60.0, 65.0],
	    'lthNight': [40.0, 45.0, 50.0, 55.0],
	    'roadSurfaceRoughness': [0.0, 0.29, 0.49, 0.67, 0.78],
	    'reflection': [0.5, 1.0, 1.5, 2.0, 2.5, 3.5],
	    'soundAbsorptionFactor': [12.5, 15.0],
	    'trafficType' : ['egyenletes', 'lassuló', 'gyorsuló']
	};
	
	const userInputValues = {
		'angleOfView' : true,
		'slopeElevation' : true
	};
	

		const FileUploadComponent = () => {
		    const [file, setFile] = React.useState(null);
		    const [fileName, setFileName] = React.useState('');
		    const [attributeData, setAttributeData] = React.useState([]);
		    const [columnNames, setColumnNames] = React.useState([]);
		    const [fileUploadSuccess, setFileUploadSuccess] = React.useState(false);
		
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
				if (!file) {
			        return;
			    }

			    if (file.name.split('.').pop() !== 'zip') {
			        alert('Csak .zip fájlt lehet felölteni.');
			        return;
			    }
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
		                const errorMessage = await response.text();
        				console.error('Failed to upload file:', errorMessage);
        				alert(`Hiba: ${errorMessage}`);
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
		    	
		    	const excludeFromCheck = ['R_Azonosító']; // Keys to exclude from the check

				// Check if all fields from columnMapping are selected except excluded ones
				const selectedFields = Object.keys(columnMapping).filter((key) => columnNames.includes(columnMapping[key]));
				const requiredFields = Object.entries(columnMapping)
				    .filter(([key, value]) => !excludeFromCheck.includes(key))
				    .map(([key, value]) => key);
				    
			    console.log('selectedFields:', selectedFields);
				console.log('requiredFields:', requiredFields);
				
				const missingFields = requiredFields.filter((field) => !selectedFields.includes(field));
				console.log('missingFields', missingFields);
				
				if (missingFields.length > 0) {
				    // Alert the user about missing fields
				    alert(`Válaszd még ki: ${missingFields.join(', ')}`);
				    return; // Stop further execution
				}

		
				// Send mappedData and fileName to '/console/saveToDatabase' endpoint
		        try {
					console.log('SaveToDatabase Request Data:', { fileName, mappedData });
		            const response1 = await fetch('/console/saveToDatabase', {
		                method: 'POST',
		                headers: {
		                    'Content-Type': 'application/json',
		                },
		                body: JSON.stringify({ fileName, mappedData }),
		            });
		
		            if (response1.ok) {
						console.log('SaveToDatabase Response:', await response1.text());
		                console.log('Data filtered and sent to backend successfully');
		                setFileUploadSuccess(true);
		                console.log('Response1 success:', fileUploadSuccess);
		            } else {
						const errorMessage = await response.text();
		                console.error('Failed to save data', errorMessage);
		                alert(`Hiba mentés közben: ${errorMessage}`);
		            }
		        } catch (error) {
		            console.error('Error filtering data:', error);
		        }
		    };
		    
			// Modify parameters FORM
			   
	     	const [showModifyForm, setShowModifyForm] = React.useState(false);
    		const [modifiedParameters, setModifiedParameters] = React.useState({});

		    const handleModifyParametersClick = () => {
		        // Show the form only if response1 was successful
		        if (attributeData.length > 0) {
		            setShowModifyForm(true);
		        } else {
		            console.error('No data available to modify parameters');
		        }
		    };

		    const handleFormSubmit = async (event) => {
		        event.preventDefault();
		
			// Create a copy of modifiedParameters to retain unchanged parameters
		    const updatedParameters = { ...defaultValues, ...modifiedParameters };
		
		        // Send modifiedParameters to '/console/saveMutableParameters' endpoint
		        try {
		            console.log('SaveMutableParameters Request Data:', updatedParameters);
		            const response2 = await fetch('/console/saveMutableParameters', {
		                method: 'POST',
		                headers: {
		                    'Content-Type': 'application/json',
		                },
		                body: JSON.stringify(updatedParameters),
		            });
		
		            if (response2.ok) {
		                console.log('Modified Parameters sent to backend successfully');
		                setShowModifyForm(false);
		                window.location.href = '/console/display';

		            } else {
						const errorMessage = await response.text();
		                console.error('Failed to save mutable params', errorMessage);
		                alert(`Hiba a paraméterek mentés közben: ${errorMessage}`);
		            }
		        } catch (error) {
		            console.error('Error sending Modified Parameters:', error);
		        }
		    };
		    
		    const isInteger = (value) => {
			    // Check if the value is an integer
			    return /^\d+$/.test(value);
			};
			
			const isDouble = (value) => {
			    // Check if the value is a double
			    return /^-?\d+(\.\d+)?$/.test(value);
			};
		
		    return (
		        <div>
		            <h1>KONZOL OLDAL</h1>
		            <input type="file" accept=".zip" onChange={handleFileChange} />
		            <button onClick={handleFileUpload}>Upload</button>
		            
		            {attributeData.length > 0 && (
		                <button onClick={handleModifyParametersClick} disabled={!fileUploadSuccess}>
		                    Modify Parameters
		                </button>
		            )}

		            {showModifyForm && (
		                <form onSubmit={handleFormSubmit}>
		                    {Object.entries(parameterMapping).map(([key, value]) => (
		                        <div key={key}>
		                            <label htmlFor={key}>{value}</label>
		                            {userInputValues[key] ? (
		                                <input
							                type="text"
							                id={key}
							                value={modifiedParameters[key] !== undefined ? modifiedParameters[key] : defaultValues[key] || ''}
							                onChange={(event) => {
							                    const { value: inputValue } = event.target;
							                    if (inputValue === '' || isInteger(inputValue) || isDouble(inputValue)) {
							                        let processedValue = inputValue;
							                        // Remove leading zeros if more numbers are typed
							                        if (processedValue.length > 1 && processedValue.startsWith('0') && !processedValue.startsWith('0.')) {
							                            processedValue = processedValue.replace(/^0+(?=\d)/, '');
							                        }
							                        setModifiedParameters({
							                            ...modifiedParameters,
							                            [key]: processedValue === '' ? undefined : processedValue,
							                        });
							                    }
							                }}
							            />
		                            ) : (
		                                <select
		                                    id={key}
		                                    value={modifiedParameters[key] || defaultValues[key]}
		                                    onChange={(event) =>
		                                        setModifiedParameters({
		                                            ...modifiedParameters,
		                                            [key]: event.target.value,
		                                        })
		                                    }
		                                >
		                                    {selectableValues[key].map((option) => (
		                                        <option key={option} value={option}>
		                                            {option}
		                                        </option>
		                                    ))}
		                                </select>
		                            )}
		                        </div>
		                    ))}
		                    <button type="submit">Submit</button>
		                </form>
		            )}
		            
		
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
		
		
				