 // Define mapping of user-friendly column names to actual column names
		    const columnMapping = {
		        'Azonosító': 'identifier',
		        'I_km/h': 'speed1',
		        'II_km/h': 'speed2',
		        'III_km/h': 'speed3',
		        'I_ak_kat_N': 'acousticCatDay1',
		        'II_ak_kat_N': 'acousticCatDay2',
		        'III_ak_kat_N': 'acousticCatDay3',
		        'I_ak_kat_E': 'acousticCatNight1',
		        'II_ak_kat_E': 'acousticCatNight2',
		        'III_ak_kat_E': 'acousticCatNight3',
		        'R_Azonosító': 'identifierR',
		        'R_I_km/h': 'speed1R',
		        'R_II_km/h': 'speed2R',
		        'R_III_km/h': 'speed3R',
		        'R_I_ak_kat_N': 'racousticCatDayR1',
		        'R_II_ak_kat_N': 'acousticCatDayR2',
		        'R_III_ak_kat_N': 'acousticCatDayR3',
		        'R_I_ak_kat_E': 'acousticCatNightR1',
		        'R_II_ak_kat_E': 'acousticCatNightR2',
		        'R_III_ak_kat_E': 'acousticCatNightR2',
		        'LAEq Nappal' : 'laeqDay',
		        'LAeq Éjjel' : 'laeqNight',
		        'LW Nappal' : 'lwDay',
		        'LW Éjjel' : 'lwNight',
		        'Hatásterület Nappal' : 'impactAreaDay',
		        'Hatásterület Éjjel' : 'impactAreaNight',
		        'Védőtávolság Nappal' : 'protectiveDistanceDay',
		        'Védőtávolság Éjjel' : 'protectiveDistanceNight'
		    };
   
		    let selectedValue = '';
		    
		    // listener for calculations div
		    function handleCalculationChange() {
			    const selectElement = document.getElementById('calculations');
			    selectedValue = selectElement.value;
			}
			    // Perform actions based on the selected option value (selectedValue)
			    function handleSubmit() {
				    if (selectedValue !== '' && activeFileId) {
						console.log('active field:', activeFileId);
				        fetch(`${selectedValue}/${activeFileId}`, {
				            method: 'POST',
				            headers: {
				                'Content-Type': 'application/json'
				            },
				            body: JSON.stringify({ fileId: activeFileId })
				        })
				        .then(response => {
				            if (!response.ok) {
				                throw new Error('Network response was not ok');
				            }
				            // Fetch the updated data and re-render the table
            				fetchDataAndRenderTabs();

				           
				        })
				        .catch(error => {
				            console.error('There was a problem with the fetch operation:', error);
				        });
				    }
			}

		
		       
			// Function to fetch data, group by file_id, and render tabs and tables
			const fetchDataAndRenderTabs = () => {
			    fetch('/console/displayData')
			        .then(response => {
			            if (!response.ok) {
			                throw new Error('Network response was not ok');
			            }
			            return response.json();
			        })
			        .then(data => {
			            console.log('Fetched Data:', data);
			            renderTabsAndTables(data);
			        })
			        .catch(error => {
			            console.error('There was a problem with the fetch operation:', error);
			        });
			};
			
			let activeFileId;
			// Function to render tabs and tables using grouped data
			const renderTabsAndTables = (groupedData) => {
			    console.log('Grouped Data:', groupedData);
			        const tabs = Object.keys(groupedData).map(fileId => {
			        const fileName = groupedData[fileId][0].fileName;
			
			        return (
			            <span key={fileId}>
			                <button onClick={() => {
			                    renderTable(groupedData[fileId]);
			                    activeFileId = fileId; // Set the active file_id
			                }}>
			                    {fileName}
			                </button>
			            </span>
			        );
			    });
			    
			    // Set activeFileId for the lowest file_id by default
			    const lowestFileId = Math.min(...Object.keys(groupedData));
			    activeFileId = lowestFileId;
			
			    ReactDOM.render(
			        <div>
			            {tabs}
			            <div id="table-container"></div>
			        </div>,
			        document.getElementById('root')
			    );
			
			    // Display the table for the lowest file_id by default
			  //  const lowestFileId = Math.min(...Object.keys(groupedData));
			    renderTable(groupedData[lowestFileId]);
			};
			
			// Function to render a specific table for a file
			const renderTable = (data) => {
			    console.log('Data for Table:', data);
			    
			    // Extracting the fileName from the data
    			const fileName = data.length > 0 ? data[0].fileName : '';
    			
    			// Displaying the table name (fileName) above the table
				const tableName = <h2>{fileName}</h2>;
			    
			     // Filter out fields that have null values based on columnMapping
			    const nonNullColumns = Object.entries(columnMapping)
			        .filter(([displayName, columnKey]) => {
			            // Check if the column exists in the data and if its value is not null
			            return data.some(item => item[columnKey] !== null && item[columnKey] !== undefined);
			        })
			        .map(([displayName, columnKey]) => {
			            return { displayName, columnKey };
			        });
			
			    const tableHeaders = nonNullColumns.map(({ displayName }, index) => (
			        <th key={index}>{displayName}</th>
			    ));
			
			    const tableRows = data.map((dataItem, index) => {
			        return (
			            <tr key={index}>
			                {nonNullColumns.map(({ columnKey }, idx) => {
			                    const cellValue = dataItem[columnKey];
			                    return <td key={idx}>{cellValue}</td>;
			                })}
			            </tr>
			        );
			    });
			
			    const table = (
					<div>
						{tableName}
				        <table>
				            <thead>
				                <tr>
				                    {tableHeaders}
				                </tr>
				            </thead>
				            <tbody>
				                {tableRows}
				            </tbody>
				        </table>
			        </div>
			    );
			    console.log('Rendering Table:', table);
			    ReactDOM.render(table, document.getElementById('table-container'));
			};
			
			// Call the function to fetch data and render tabs on page load
			fetchDataAndRenderTabs();