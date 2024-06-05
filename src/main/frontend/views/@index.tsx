import { VerticalLayout } from '@vaadin/react-components/VerticalLayout';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';


export const config: ViewConfig = { menu: { order: 0, icon: 'line-awesome/svg/home-solid.svg' }, title: 'Home' };

export default function HomeView() {
  return (
      <>
        <section className="hero-section">
          <div className="hero-content flex justify-center items-center h-screen layout-div">
            <VerticalLayout>
              <h2 className="text-center">Document Intelligence Application</h2>
              <span>This is a prototype that uses Azure Document Intelligence services to analyze invoices.</span>
                <span>Select your file and click on the 'Analyse Document' button to retrieve key details of the invoice.</span>
            </VerticalLayout>
          </div>
        </section>
      </>
  );
}
