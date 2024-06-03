import { useRouter } from 'next/router'; 
import { Button } from "./ui/button";

export function Footer() {
    const router = useRouter(); 
  
    const isTasksPage = router.pathname === '/tasks';
  
    const handleEndSessionClick = () => {
        router.push('/end_session'); 
    };
  
    return (
      isTasksPage ? (
        <footer className="w-full flex fixed bottom-0 right-0 p-1 z-50 bg-transparent">
          <div className="px-1 w-full flex flex-row justify-end">
            <Button 
              variant="ghost" 
              type="submit" 
              className="py-6 px-20 mb-5 mr-5 bg-red-500 hover:bg-red-600 text-white rounded-full text-lg mt-4"
              onClick={handleEndSessionClick}
            >
              End Search Session
            </Button>
          </div>
        </footer>
      ) : null 
    );
}