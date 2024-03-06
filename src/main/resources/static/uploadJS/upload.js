
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
	
	function checkSession() {
	    fetch("/session/check-session")
	        .then(response => {
				console.log('session check invoked');
	            if (!response.ok) {
					console.log('status', response.status);
	                if (response.status === 401) { // Unauthorized (session expired)
	              //      window.location.replace("/login?sessionExpired=true");
	                    window.location.href = "/login?sessionExpired=true";
	                } else {
	                    throw new Error('Network response was not ok');
	                }
	            }
	  
	        });
	}
	

		const FileUploadComponent = () => {
		    const [file, setFile] = React.useState(null);
		    const [mappedData, setMappedData] = React.useState(null);
		    const [fileName, setFileName] = React.useState('');
		    const [attributeData, setAttributeData] = React.useState([]);
		    const [columnNames, setColumnNames] = React.useState({});
		    const [originalColumnNames, setOriginalColumnNames] = React.useState([]);
		    const [fileUploadSuccess, setFileUploadSuccess] = React.useState(false);
		    const [isTableLoaded, setIsTableLoaded] = React.useState(false);
		    const [showModifyForm, setShowModifyForm] = React.useState(false);
    		const [modifiedParameters, setModifiedParameters] = React.useState({});
		    const [isFlashingUpload, setIsFlashingUpload] = React.useState(false);
		    const [isFlashingFilter, setIsFlashingFilter] = React.useState(false);
		    const [isFlashingModify, setIsFlashingModify] = React.useState(false);
		    const [isFlashingModifySubmit, setIsFlashingModifySubmit] = React.useState(false);
		    const [hasSavedData, setHasSavedData] = React.useState(false);
		    const [loading, setLoading] = React.useState(false);
		    let columnNamesAreEqual = false;
		    
		    React.useEffect(() => {
			    // Fetch data to check if there's saved data
			    fetch('/console/checkSavedData')
			        .then(response => response.json())
			        .then(hasSavedData => {
			            setHasSavedData(hasSavedData);
			        })
			        .catch(error => {
			            console.error('Error fetching saved data:', error);
			        });
			}, []);
		
		    const handleFileChange = (event) => {
				checkSession();
	//			document.body.style.cursor = "wait"; 
				setLoading(true);
		        const selectedFile = event.target.files[0];
		        const fileName = selectedFile.name;
		        
		        if (file) {
			      // Reset the previous file and related states
			      setFile(null);
			      setMappedData(null);
			      setFileName('');
			      setFileUploadSuccess(false);
			      setIsTableLoaded(false);
			      setShowModifyForm(false);
			      setModifiedParameters({});
			      setIsFlashingUpload(false);
			      setIsFlashingFilter(false);
			      setIsFlashingModify(false);
			      setIsFlashingModifySubmit(false);
			      columnNamesAreEqual = false;
			    }
		        
		        setFile(selectedFile);
		        setFileName(fileName);
		        setIsFlashingUpload(true);
		        setShowModifyForm(false);
			//	document.body.style.cursor = "auto"; 
				setLoading(false);
		    };

		
		    const filterTableBySelectedColumns = () => {
				checkSession();
				setLoading(true);
			//	document.body.style.cursor = "wait"; 
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
		       // document.body.style.cursor = "auto";
		       	setLoading(false);
		        return filteredData;
		    };

		
		    const handleFileUpload = async () => {
			checkSession();
			        
			//	document.body.style.cursor = "wait";
			setLoading(true);
				if (!file) {
					setLoading(false);
			        return;
			    }

			    const fileExtension = file.name.split('.').pop().toLowerCase();
			    if (fileExtension !== 'zip') {
			        alert('Csak .zip fájlt lehet felölteni.');
					setLoading(false);
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
		                
		                setOriginalColumnNames(Object.keys(data[0]));
		                
		                const currentFileColumnNames = Object.keys(data[0]);
		                
		                setAttributeData(data);

		                
		                // Check if there are previous column selections in session storage
					    const storedColumnSelection = getColumnSelection();
					    console.log('storedColumnSelection', storedColumnSelection);
					    const storedOriginalColumnNames = getOriginalColumnNames();
				      
						const arraysEqual = (arr1, arr2) => {
						  if (!arr1 || !arr2) {
						    return false; 
						  }
						
						  if (arr1.length !== arr2.length) {
						    return false;
						  }
						
						  for (let i = 0; i < arr1.length; i++) {
						    if (arr1[i] !== arr2[i]) {
						      return false; 
						    }
						  }
						
						  return true; 
						};

						columnNamesAreEqual = arraysEqual(storedOriginalColumnNames, currentFileColumnNames);
						
						console.log('setcolumnNames', columnNamesAreEqual);
						console.log('storedOriginalCOlumnNames', storedOriginalColumnNames);
						console.log('currentFileColumnNames', currentFileColumnNames);
						console.log('columnNamesAreEqual', columnNamesAreEqual);
	
				      if (storedColumnSelection && columnNamesAreEqual) {
						  console.log('selection is true');
				        setColumnNames(storedColumnSelection);
				      } else {
						  console.log('selection is false');
				        setColumnNames(Array(data[0] ? Object.keys(data[0]) : []).fill(''));
				      }
		                setIsTableLoaded(true);
		                setIsFlashingUpload(false);
		                setIsFlashingFilter(true);
		                console.log('file column names', columnNames);
		                console.log('original names', Object.keys(data[0]));
		                setLoading(false);
		            } else {
		                const errorMessage = await response.text();
        				console.error('Failed to upload file:', errorMessage);
        				alert(`Hiba: ${errorMessage}`);
        				setLoading(false);
		            }
			        } catch (error) {
			            console.error('Error uploading file:', error);
			        } finally {
				//		document.body.style.cursor = "auto";
						setLoading(false);;
						
					}
		    };
		      
		    const saveColumnSelection = (columnSelection) => {
			  sessionStorage.setItem('columnSelection', JSON.stringify(columnSelection));
			  sessionStorage.setItem('originalColumnNames', JSON.stringify(originalColumnNames));
			};
			
			// Render options for the select element
			const renderOptions = (index, selectedValue) => {
			    const options = [<option key="" value="">Select Column Name</option>];
			    for (const [displayName, backendName] of Object.entries(columnMapping)) {
			        if (!Object.values(columnNames).includes(backendName) || backendName === selectedValue) {
			            options.push(
			                <option key={backendName} value={backendName}>
			                    {displayName}
			                </option>
			            );
			        }
			    }
			    return options;
			};

			const getColumnSelection = () => {
			  const storedColumnSelection = sessionStorage.getItem('columnSelection');
			  const parsedColumnSelection = storedColumnSelection ? JSON.parse(storedColumnSelection) : null;
			
			    // Replace null values with an empty string
			    return parsedColumnSelection ? parsedColumnSelection.map(value => value || '') : null;
			};
			const getOriginalColumnNames = () => {
				const storedOriginalColumnNames = sessionStorage.getItem('originalColumnNames');
				return storedOriginalColumnNames ? JSON.parse(storedOriginalColumnNames) : null;
			}
		    
		    const handleSelectChange = (index) => (event) => {
		        const updatedColumnNames = [...columnNames];
		        updatedColumnNames[index] = event.target.value;
		        setColumnNames(updatedColumnNames);
		        
		        // Save the column selections to session storage
  				saveColumnSelection(updatedColumnNames);
		    };
		    

		    const handleFilterClick = async () => {
				checkSession();
			//	document.body.style.cursor = "wait";
				setLoading(true);
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
				
				// Check if all required fields are selected
				const missingFields = requiredFields.filter((field) => !selectedFields.includes(field));
				
				if (missingFields.length > 0) {
				    // Alert the user about missing fields
				    alert(`Válaszd még ki: ${missingFields.join(', ')}`);
				    return;
				}

				setMappedData(mappedData);
				console.log('mappedData after filter: ', mappedData);
				setFileUploadSuccess(true);
                setIsFlashingFilter(false);
                setIsFlashingModify(true);
               // document.body.style.cursor = "auto";
               	setLoading(false);
		    };
		    
		    const handleModifyParametersClick = () => {
				checkSession();
			//	document.body.style.cursor = "wait";
				setLoading(true);
		        // Show the form only if response1 was successful
		        if (attributeData.length > 0) {
					console.log('SaveToDatabase Request Data:', { fileName, mappedData });
		            setShowModifyForm(true);
		            setIsFlashingModify(false);
		            setIsFlashingModifySubmit(true);
		           // document.body.style.cursor = "auto";
		           setLoading(false);
		        } else {
		            console.error('No data available to modify parameters');
		        }
		    };

		    const handleFormSubmit = async (event) => {
				checkSession();
			//	document.body.style.cursor = "wait";
				setLoading(true);
		        event.preventDefault();
		
			// Create a copy of modifiedParameters to retain unchanged parameters
		    const updatedParameters = { ...defaultValues, ...modifiedParameters };
		    
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
		                setIsFlashingFilter(false);
		                setIsFlashingModify(true);
		                console.log('Response1 success:', fileUploadSuccess);
		            } else {
						const errorMessage = await response.text();
		                console.error('Failed to save data', errorMessage);
		                alert(`Hiba mentés közben: ${errorMessage}`);
		            }
		        } catch (error) {
		            console.error('Error filtering data:', error);
		        } finally {
				//	document.body.style.cursor = "auto";
					setLoading(false);
				}
		
		        // Send modifiedParameters to '/console/saveMutableParameters' endpoint
		        try {
					//document.body.style.cursor = "wait";
					setLoading(true);
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
		                setIsFlashingModifySubmit(false);
		                setShowModifyForm(false);
		                window.location.href = '/console/display';

		            } else {
						const errorMessage = await response.text();
		                console.error('Failed to save mutable params', errorMessage);
		                alert(`Hiba a paraméterek mentés közben: ${errorMessage}`);
		            }
		        } catch (error) {
		            console.error('Error sending Modified Parameters:', error);
		        } finally {
				//	document.body.style.cursor = "auto";
					setLoading(false);
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
			        <div className="container-fluid mb-5 pb-5">
			        	{loading && (			    
			                    <div className="spinner-border spinner" role="status">
			                        <span>Loading...</span>
			                    </div>			               
			            )}
			            
			            <div className="row mt-5 pt-5">
        					<div className="col-md">
			            
					            <div id="buttons">
					             <label id="browseFile" className="btn btn-dark" >
							        Fájl kiválasztása
							        <input type="file" style={{ display: 'none' }} accept=".zip" onChange={handleFileChange} />
							    </label>
		
			    				
					            <button id="uploadButton" className={`btn btn-dark ${isFlashingUpload ? 'flashing' : ''}`} onClick={handleFileUpload} disabled={!file || isTableLoaded}>Feltöltés</button>
					            
					            <button id="filterButton" className={`btn btn-dark ${isFlashingFilter ? 'flashing' : ''}`} onClick={handleFilterClick} disabled={!isTableLoaded || fileUploadSuccess}>Oszlopok szűrése</button>
					            
					            {attributeData.length > 0 && (
					                <button id="modifyButton" className={`btn btn-dark ${isFlashingModify ? 'flashing' : ''}`} onClick={handleModifyParametersClick} disabled={!fileUploadSuccess}>
					                    Számítási paraméterek
					                </button>
					            )}
					            
					            {hasSavedData && (
			                        <a href="/console/display" id="goBackLink" className="btn btn-dark text-success" role="button">
			                            Vissza a számításokhoz!
			                        </a>
			                    )}
					            
					            </div>
  							</div>
  						</div>
				<div className="container mt-5 pt-1">
				    <div className="row">
				        <div className="col-md-6">
				            <div id="form">
				                {showModifyForm && (
				                    <form id="formModify" className="mb-3 mt-3" onSubmit={handleFormSubmit}>
				                        <div className="row">
				                            {Object.entries(parameterMapping).map(([key, value], index) => (
				                                <div key={key} className="col-md-6 mb-3">
				                                    <label htmlFor={key}>{value}</label>
				                                    {userInputValues[key] ? (
				                                        <input
				                                            className="form-control"
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
				                                            className="form-select"
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
				                        </div>
				                        <button type="submit" className={`btn btn-dark ${isFlashingModifySubmit ? 'flashing' : ''}`}>
				                            OK
				                        </button>
				                    </form>
				                )}
				            </div>
				        </div>
				
				    </div>
				</div>
		            
		             <div className="row mt-5 mb-5 pb-5 pt-1">
        				<div className="col-md">
		            
							<div id="tableContainer" className={`table-responsive mx-auto ${isFlashingUpload ? 'd-none' : ''}`}>
					            <table id="uploadTable" className="table table-dark table-striped table-sm table-hover caption-top">
					            {isTableLoaded && <caption>{fileName}</caption>}
					                <thead>
					                    <tr>
					                        {attributeData.length > 0 &&
					                            Object.keys(attributeData[0]).map((key, index) => (
					                                <th key={key}>
					                                    <div>
					                                        <span>{key}</span>
					                                        <select value={columnNames[index]} onChange={handleSelectChange(index)}>
					                                            {renderOptions(index, columnNames[index])}
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
					
					            
				            </div>
				        </div>
		        
		        	</div>
      			</div>
		    );
		};
		checkSession();
		ReactDOM.render(<FileUploadComponent />, document.getElementById('root'));
		
		
				