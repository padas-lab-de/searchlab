import React, { useState } from 'react';
import { SearchResult } from "@/types";

interface SERPResultsProps {
    results: SearchResult[];
}

const SERPResults: React.FC<SERPResultsProps> = ({ results }) => {
    const [relevantResults, setRelevantResults] = useState<Record<number, boolean>>({});

    const toggleRelevance = (index: number, event: React.MouseEvent<SVGSVGElement, MouseEvent>) => {
        event.stopPropagation(); // Prevent triggering the link
        setRelevantResults(prev => ({ ...prev, [index]: !prev[index] }));
    };

    const truncateContent = (content: string) => {
        return content.length > 320 ? content.substring(0, 320) + '...' : content;
    };

    // SVG Star Icon with dynamic styling based on relevance
    const StarIcon = ({ isRelevant, onClick }: { isRelevant: boolean; onClick: (event: React.MouseEvent<SVGSVGElement, MouseEvent>) => void; }) => (
        <svg onClick={onClick} className={`ml-auto w-6 h-6 ${isRelevant ? 'text-yellow-500' : 'text-gray-400 dark:text-gray-600'}`} xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" style={{ minWidth: '24px', minHeight: '24px' }}>
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.176 0l-2.8 2.034c-.785.57-1.84-.197-1.54-1.118l1.07-3.292a1 1 0 00-.364-1.118l-2.8-2.034c-.783-.57-.38-1.81.588-1.81h3.462a1 1 0 00.95-.69l1.07-3.292z" />
        </svg>
    );


    return (
        <div className="container mx-auto px-4">
            {results.map((result, index) => (
                <div key={index} className="mb-8 flex justify-between items-start" onClick={() => { }}>
                    <div className="flex-grow">
                        <a href={result.url} target="_blank" rel="noopener noreferrer" className="text-sm text-blue-500 hover:text-blue-400 dark:text-blue-400 dark:hover:text-blue-300">
                            {result.url}
                        </a>
                        <a href={result.url} target="_blank" rel="noopener noreferrer">
                            <h3 className="text-lg text-blue-500 font-medium hover:underline dark:text-blue-400 flex items-center">
                                {result.title}
                            </h3>
                        </a>
                        <p className="text-sm text-gray-600 dark:text-gray-300">{truncateContent(result.content)}</p>
                    </div>
                    <StarIcon isRelevant={!!relevantResults[index]} onClick={(event) => toggleRelevance(index, event)} />
                </div>
            ))}
        </div>
    );
};

export default SERPResults;